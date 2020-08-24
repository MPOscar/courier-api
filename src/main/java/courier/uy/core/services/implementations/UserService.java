package courier.uy.core.services.implementations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import courier.uy.core.db.*;
import courier.uy.core.resources.dto.UsuarioBasic;
import courier.uy.core.auth.jwt.Roles;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.LoginResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.security.jwt.JwtTokenProvider;
import courier.uy.core.services.interfaces.ILoginService;
import courier.uy.core.services.interfaces.IUserService;
import courier.uy.core.utils.IEmailHelper;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;

import courier.uy.core.entity.CodigoUsuario;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Param;
import courier.uy.core.entity.ReseteoContrasena;
import courier.uy.core.entity.Rol;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;

@Service
public class UserService implements IUserService {

	@Autowired
	private UsuariosDAO userRepository;
	@Autowired
	private EmpresasDAO empresasDAO;
	@Autowired
	private CodigosUsuariosDAO userCodesRepository;
	@Autowired
	private ReseteoContrasenaDAO passwordResetRepository;
	@Autowired
	private ParamsDAO paramsDAO;
	@Autowired
	private UsuarioEmpresaDAO usuarioEmpresaDAO;
	@Autowired
    JwtTokenProvider jwtTokenProvider;

	private IEmailHelper emailHelper;
	private ILoginService loginService;

	public UserService(UsuariosDAO usuariosDAO, CodigosUsuariosDAO codigosUsuariosDAO, IEmailHelper emailHelper,
			ParamsDAO paramsDAO, ILoginService loginService, ReseteoContrasenaDAO passwordResetDAO,
			UsuarioEmpresaDAO usuarioEmpresaDAO) {
		this.userRepository = usuariosDAO;
		this.userCodesRepository = codigosUsuariosDAO;
		this.emailHelper = emailHelper;
		this.paramsDAO = paramsDAO;
		this.loginService = loginService;
		this.passwordResetRepository = passwordResetDAO;
		this.usuarioEmpresaDAO = usuarioEmpresaDAO;
	}

	@Transactional
	@Override
	public Optional<Usuario> obtenerDatosUsuarioEmpresa(String idUsuario, UsuarioEmpresa ue)
			throws ServiceException, Exception {

		Usuario usuario = this.userRepository.findById(idUsuario);
		Optional<Usuario> result = Optional.ofNullable(usuario);
		if (!result.isPresent())
			throw new ServiceException("El Usuario no existe o su Usuario no tiene permisos para obtener sus datos");

		Set<Company> listaEmpresas = new HashSet<Company>(usuario.getEmpresas());
		Boolean perteneceEmpresa = listaEmpresas.contains(ue.getEmpresa());

		if (!perteneceEmpresa) {
			throw new ServiceException("El Usuario no pertenece a la Empresa " + ue.getEmpresa().getNombre());
		}

		return result;
	}

	@Override
	public Usuario Register(Usuario u) throws ServiceException {
		u.setParametersForRegister();
		this.userRepository.usuarioNoEstaRepetido(u);
		this.userRepository.save(u);
		this.sendValidationEmail(u);
		return u;
	}

	@Override
	public void enviarEmailConfirmacion(Usuario user) throws ServiceException {
		Optional<Usuario> existingEmailUser = this.userRepository.findByEmail(user.getEmail());

		existingEmailUser.filter(u -> !u.getValidado())
				.orElseThrow(() -> new ServiceException("Este Usuario ya fué validado o no existe"));
		this.sendValidationEmail(existingEmailUser.get());
	}

	@Transactional
	@Override
	public LoginResponse Confirm(String code) throws ServiceException, JoseException {
		Optional<CodigoUsuario> codigoOpt = this.userCodesRepository.findByCodigo(code);
		if (codigoOpt.isPresent()) {
			CodigoUsuario codigo = codigoOpt.get();
			Usuario u = this.userRepository.findById(codigo.getUsuario().getId());
			if (codigo.fueUsado()) {
				u.setValidado(true);
				this.userRepository.save(u);
				throw new ServiceException("Este usuario ya está confirmado");
			} else if (codigo.estaExpirado()) {
				this.sendValidationEmail(u);
				throw new ServiceException("Este código ya expiró. Por favor revise su email nuevamente");
			} else {
				codigo.usar();
				this.userCodesRepository.update(codigo);
				u.setValidado(true);
				this.userRepository.save(u);
			}
			Param jwtKey = paramsDAO.findByNombre("JWT_SECRET_KEY");
			Param expTime = paramsDAO.findByNombre("TOKEN_EXP_TIME");
			List<String> allRoles = new ArrayList<String>();
			JsonWebSignature jws;

			jws = jwtTokenProvider.buildToken(u, null, jwtKey, Float.parseFloat(expTime.getValor()));

			String jwt = jws.getCompactSerialization();

			JsonWebEncryption jwe = encryptToken(jwt, jwtKey);
			String token = jwe.getCompactSerialization();
			if (u.esAdministradorSistema())
				allRoles.add("systemAdmin");
			Set<Company> businesses = new HashSet<>();
			for (UsuarioEmpresa ue : u.getUsuariosEmpresas()) {
				businesses.add(ue.getEmpresa());
			}

			return new LoginResponse(token, allRoles, u, businesses, null);
		} else {
			throw new ServiceException("Este código no existe, por favor verifique su email");
		}

	}

	@Transactional
	@Override
	public LoginResponse AcceptInvitation(String code) throws ServiceException, Exception {
		Optional<CodigoUsuario> codigoOpt = this.userCodesRepository.findByCodigo(code);
		if (codigoOpt.isPresent()) {
			CodigoUsuario codigo = codigoOpt.get();
			if (!codigo.estaExpirado() && !codigo.fueUsado()) {
				Usuario u = this.userRepository.findById(codigo.getUsuario().getId());
				List<UsuarioEmpresa> usuariosEmpresa = usuarioEmpresaDAO.obtenerUsuariosEmpresaPorUsuario(u);
				for (UsuarioEmpresa ue : usuariosEmpresa) {
					if (ue.getEmpresa().getId().equals(codigo.getEmpresa().getId())) {
						ue.setActivo(true);
						ue.setValidado(true);
						usuarioEmpresaDAO.save(ue);
					}
				}

				if (codigo.fueUsado()) {
					u.setValidado(true);
					this.userRepository.save(u);
					throw new ServiceException("Este usuario ya está confirmado");
				} else if (codigo.estaExpirado()) {
					this.sendValidationEmail(u);
					throw new ServiceException("Este código ya expiró. Por favor revise su email nuevamente");
				} else {
					codigo.usar();
					this.userCodesRepository.update(codigo);
					u.setValidado(true);
					this.userRepository.save(u);
				}
				Optional<String> optId = Optional.empty();
				LoginResponse response = this.loginService.Login(u, optId);
				return response;

			} else {
				throw new ServiceException("Este código fué usado o expiró. Se debe enviar una nueva invitación");
			}

		} else {
			throw new ServiceException("Este código no existe, por favor verifique su email");
		}

	}

	private JsonWebSignature buildToken(Usuario usuario, UsuarioEmpresa usuarioEmpresa, Param jwtKey, Float expTime) {
		final JwtClaims claims = new JwtClaims();

		claims.setSubject(usuario.getId().toString());
		claims.setStringClaim("usuario", usuario.getUsuario());

		if (usuarioEmpresa != null) {
			claims.setStringClaim("usuario_empresa", "" + usuarioEmpresa.getId());
			claims.setStringClaim("empresa", usuarioEmpresa.getEmpresa().getGln());
			claims.setStringClaim("rol", usuarioEmpresa.getRol());
		}

		claims.setIssuedAtToNow();
		claims.setGeneratedJwtId();
		claims.setExpirationTimeMinutesInTheFuture(expTime);

		final JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
		jws.setKey(new HmacKey(jwtKey.getValor().getBytes()));

		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

		return jws;
	}

	private JsonWebEncryption encryptToken(String jwt, Param jwtKey) {
		JsonWebEncryption jwe = new JsonWebEncryption();

		jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT);
		jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
		jwe.setKey(new HmacKey(jwtKey.getValor().getBytes()));
		jwe.setContentTypeHeaderValue("JWT");
		jwe.setPayload(jwt);

		return jwe;
	}

	private void sendValidationEmail(Usuario usuario) throws ServiceException {
		CodigoUsuario userCode = new CodigoUsuario(usuario);
		this.userCodesRepository.save(userCode);
		this.emailHelper.SendRegisterEmail(usuario.getEmail(), usuario.getNombre(), userCode.getCodigo());
	}

	private CodigoUsuario createUserCode(String userId, String businessId) {
		Usuario usuario = userRepository.findById(userId);
		Company empresa = empresasDAO.findById(businessId);
		CodigoUsuario toReturn = new CodigoUsuario(usuario);
		toReturn.setEmpresa(empresa);
		toReturn.setSempresa(empresa.getId());
		toReturn.setSusuario(usuario.getId());
		return toReturn;
	}

	private UsuarioEmpresa createBusinessUser(Usuario user, Company business, Set<Rol> roles) {
		UsuarioEmpresa newUE = new UsuarioEmpresa();
		newUE.setActivo(false);
		newUE.setValidado(false);
		Set<String> sroles = new HashSet<String>();
		for (Rol rol: roles) {
			sroles.add(rol.getId());
		}
		newUE.setRoles(roles);
		newUE.setSroles(sroles);
		newUE.setRol(Roles.BUSINESS_ADMIN);
		newUE.setEmpresa(business);
		newUE.setSempresa(business.getId());
		newUE.setUsuario(user);
		newUE.setSusuario(user.getId());
		return newUE;
	}

	@Transactional
	@Override
	public Usuario InviteToBusiness(UsuarioPrincipal existing, Usuario newUser) throws ServiceException, ModelException {
		UsuarioEmpresa existingUE = existing.getUsuarioEmpresa();
		newUser.setParametersForRegister();
		newUser.setValidado(false);
		if (newUser.getRoles() == null) {
			throw new ServiceException("Deben ser enviados los Roles que tendrá el nuevo Usuario");
		}

		Optional<Usuario> existingEmail = userRepository.findByEmail(newUser.getEmail());
		Boolean needsUser = true;
		Boolean needsBusinessUser = true;
		Usuario existingUser = existingEmail.orElse(userRepository.findByUsuario(newUser.getUsuario()).orElse(null));

		if (existingUser != null) {
			needsUser = false;

			List<UsuarioEmpresa> usuariosEmpresa = usuarioEmpresaDAO.obtenerUsuariosEmpresaPorUsuario(existingUser);
			for (UsuarioEmpresa ue : usuariosEmpresa) {
				if (ue.getEmpresa().getId().equals(existingUE.getEmpresa().getId())) {// tengo ue para empresa pero falta validar?
					if (ue.getActivo())
						throw new ServiceException("Este Usuario ya está en uso con esta empresa");
					needsBusinessUser = false;
					Set<Rol> roles = new HashSet<Rol>();
					roles.addAll(newUser.getRoles());
					ue.setRoles(roles);
					ue.setEliminado(false);
				}
			}
		}

		if (!needsBusinessUser) {// hay usuario y usuarioEmpresa, hay que agarrar codigo existente, borrarlo,
								 // crear uno nuevo y enviarlo, actualizar roles!
			Optional<CodigoUsuario> existingUserCode = this.userCodesRepository
					.findByUserAndBusiness("" + existingUser.getId(), "" + existingUE.getEmpresa().getId());
			if (existingUserCode.isPresent()) {
				CodigoUsuario codeToExpire = existingUserCode.get();
				codeToExpire.expirar();
				this.userCodesRepository.update(codeToExpire);
			}
			CodigoUsuario newUserCode = this.createUserCode(existingUser.getId(), existingUE.getEmpresa().getId());
			this.userCodesRepository.save(newUserCode);
			this.emailHelper.SendInvitationEmail(newUser.getEmail(), existingUE.getEmpresa().getNombre(),
					newUserCode.getCodigo());
		} else if (!needsUser) {// hay usuario pero no para esta empresa, crear businessUser, crear codigo,
								// enviarlo
			Set<Rol> roles = new HashSet<Rol>();
			roles.addAll(newUser.getRoles());
			UsuarioEmpresa newUE = this.createBusinessUser(existingUser, existingUE.getEmpresa(), roles);
			newUE = this.usuarioEmpresaDAO.save(newUE);
			existingUser.getUsuariosEmpresas().add(newUE);
			userRepository.save(existingUser);
			CodigoUsuario newUserCode = this.createUserCode(existingUser.getId(), existingUE.getEmpresa().getId());
			this.userCodesRepository.save(newUserCode);
			this.emailHelper.SendInvitationEmail(newUser.getEmail(), existingUE.getEmpresa().getNombre(),
					newUserCode.getCodigo());
		} else {// falta crear usuario luego crear usuarioEmpresa, codigo, enviarlo
			Set<Rol> roles = new HashSet<Rol>();
			roles.addAll(newUser.getRoles());
			newUser = this.userRepository.save(newUser);
			UsuarioEmpresa newUE = this.createBusinessUser(newUser, existingUE.getEmpresa(), roles);
			newUE = usuarioEmpresaDAO.save(newUE);
			newUser.getUsuariosEmpresas().add(newUE);
			newUser.getSusuariosEmpresas().add(newUE.getId());
			this.userRepository.save(newUser);
			CodigoUsuario newUserCode = this.createUserCode(newUser.getId(), existingUE.getEmpresa().getId());
			this.userCodesRepository.save(newUserCode);
			this.emailHelper.SendInvitationEmail(newUser.getEmail(), existingUE.getEmpresa().getNombre(),
					newUserCode.getCodigo());
		}
		return newUser;
	}

	@Override
	public List<Usuario> GetAllFromBusiness(Company empresa) throws ServiceException {
		String empresaId = empresa.getId();
		Set<Usuario> usuarios = new HashSet<Usuario>();
		try {

			List<UsuarioEmpresa> results = this.usuarioEmpresaDAO.obtenerUsuariosEmpresaPorEmpresa(empresaId);

			for (UsuarioEmpresa result : results) {
				Set<Rol> listadoRoles = new HashSet<>();
				listadoRoles.addAll(result.getRoles());
				Usuario usuario = result.getUsuario();
				usuario.setRoles(listadoRoles);
				usuario.setValidadoParaEmpresa(result.getValidado());
				usuarios.add(usuario);
			}

			List<Usuario> result = new LinkedList<Usuario>();
			result.addAll(usuarios);
			return result;
		} catch (ModelException e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Transactional
	@Override
	public void ModifyEmployee(UsuarioPrincipal existing, Usuario toEdit) throws ServiceException {
		Company business = existing.getUsuarioEmpresa().getEmpresa();
		Usuario bddUser = this.userRepository.findById(toEdit.getId());

		Boolean sameBusiness = false;
		for (UsuarioEmpresa ue : bddUser.getUsuariosEmpresas()) {
			Company emp = ue.getEmpresa();
			if (emp.getId() == business.getId()) {
				Set<Rol> roles = new HashSet<Rol>();
				roles.addAll(toEdit.getRoles());
				ue.setRoles(null);
				ue.setRoles(roles);
				ue.setRol(Roles.BUSINESS_ADMIN);

				sameBusiness = true;
				break;
			}
		}
		if (!sameBusiness) {
			throw new ServiceException("Solo se puede editar un usuario de la misma empresa");
		}
		if (bddUser == null) {
			throw new ServiceException("No hay usuario con este id");
		}
		bddUser.setNombre(toEdit.getNombre());
		bddUser.setApellido(toEdit.getApellido());
		String sha256hex = Hashing.sha256().hashString(toEdit.getContrasena(), StandardCharsets.UTF_8).toString();
		bddUser.setContrasena(sha256hex);
		if (!bddUser.getUsuario().equals(toEdit.getUsuario())) {
			Optional<Usuario> existingUsername = this.userRepository.findByUsuario(toEdit.getUsuario());
			if (existingUsername.isPresent())
				throw new ServiceException(
						"El nombre de usuario " + existingUsername.get().getUsuario() + " ya existe");
		}
		if (!bddUser.getEmail().equals(toEdit.getEmail())) {
			Optional<Usuario> existingEmail = this.userRepository.findByEmail(toEdit.getEmail());
			if (existingEmail.isPresent())
				throw new ServiceException("El email " + existingEmail.get().getEmail() + " ya existe");
		}
		bddUser.setUsuario(toEdit.getUsuario());
		bddUser.setEmail(toEdit.getEmail());

		this.userRepository.save(bddUser);

	}

	@Override
	public Usuario FinishRegister(Usuario usuario) throws ServiceException {
		Optional<Usuario> existing = this.userRepository.findByEmail(usuario.getEmail());
		existing.filter((e) -> {
			return e.getContrasena() == null;
		}).orElseThrow(() -> new ServiceException("No existe un Usuario no registrado con el mail especificado"));

		if (usuario.getUsuario() != null && !usuario.getUsuario().isEmpty()) {
			Optional<String> existingByUser = Optional.ofNullable(usuario.getUsuario());
			existingByUser.flatMap((u) -> {
				return this.userRepository.findByUsuario(u);
			}).filter(u -> u.getId().equals(existing.get().getId())).orElseThrow(() -> {
				return new ServiceException("Ya existe un Usuario con el nombre de Usuario especificado");
			});
		}

		Usuario existingUser = existing.get();

		Optional<String> usuarioNombreOptional = Optional.ofNullable(usuario.getUsuario());
		String usuarioNombre = usuarioNombreOptional.orElse(
				existing.get().getUsuario() == null || existing.get().getUsuario().isEmpty() ? existing.get().getEmail()
						: existing.get().getUsuario());

		existingUser.setUsuario(usuarioNombre);
		String sha256hex = Hashing.sha256().hashString(usuario.getContrasena(), StandardCharsets.UTF_8).toString();
		existingUser.setContrasena(sha256hex);
		existingUser.setNombre(usuario.getNombre());
		existingUser.setApellido(usuario.getApellido());
		this.userRepository.save(existingUser);
		return existingUser;
	}

	@Transactional
	@Override
	public void SendPasswordReset(UsuarioPrincipal existingUser, Usuario toReset) throws ServiceException, Exception {
		if (toReset.getId() == null) {
			Optional<Usuario> optUser = this.userRepository.findByEmail(toReset.getEmail());
			if (optUser.isPresent())
				toReset = optUser.get();
			else
				throw new ServiceException("No hay usuario con este email");
		}
		Usuario user = this.userRepository.findById(toReset.getId());
		Company business = existingUser.getUsuarioEmpresa().getEmpresa();
		boolean isAdminForUser = false;

		for (UsuarioEmpresa userUE : user.getUsuariosEmpresas()) {
			if (userUE.getEmpresa().getId() == business.getId()) {
				isAdminForUser = true;
			}
		}

		if (isAdminForUser) {
			sendPasswordReset(user);
		} else
			throw new ServiceException(
					"Solo un Administrador puede solicitar un reseteo de contraseña para alguien de su Empresa");

	}

	private void sendPasswordReset(Usuario usuario) throws ServiceException {
		ReseteoContrasena passwordReset = new ReseteoContrasena(usuario);
		this.passwordResetRepository.save(passwordReset);
		this.emailHelper.SendPasswordReset(usuario.getEmail(), usuario.getNombre(), passwordReset.getCodigo());
	}

	@Override
	public LoginResponse ChangePassword(String code, String contrasena) throws ServiceException, Exception {
		Optional<ReseteoContrasena> passwordResetOpt = this.passwordResetRepository.findByCodigo(code);
		if (passwordResetOpt.isPresent()) {
			ReseteoContrasena passwordReset = passwordResetOpt.get();
			/*
			 * if(passwordReset.fueUsado()) { throw new
			 * ServiceException("Este código ya fue utilizado"); }
			 */
			Date created = passwordReset.getFechaCreacion().toDate();
			Date now = new Date();

			if (passwordReset.estaExpirado() || now.getTime() - created.getTime() >= 30 * 60 * 1000) {
				throw new ServiceException("La solicitud expiró, debes repetir el procedimiento");
			}
			Usuario usuario = this.userRepository.findById(passwordReset.getUsuario().getId());
			usuario.encryptAndSetContrasena(contrasena);
			this.userRepository.save(usuario);
			Optional<String> optId = Optional.empty();
			passwordReset.usar();
			this.passwordResetRepository.save(passwordReset);
			LoginResponse response = this.loginService.Login(usuario, optId);
			return response;

		}
		throw new ServiceException("No se encuentra la solicitud para este usuario");
	}

	@Override
	public void SendPasswordReset(UsuarioBasic usuario) throws ServiceException, Exception {
		Optional<Usuario> optUser = this.userRepository.findByEmail(usuario.getEmail());
		if (optUser.isPresent()) {
			sendPasswordReset(optUser.get());
		} else
			throw new ServiceException("No hay usuario con este email");

	}

	@Override
	public void ReSendInvitation(UsuarioPrincipal businessAdmin, Usuario toSend) throws ServiceException, Exception {
		UsuarioEmpresa existingUE = businessAdmin.getUsuarioEmpresa();
		if (toSend.getId() == null) {
			Optional<Usuario> optUser = this.userRepository.findByEmail(toSend.getEmail());
			if (optUser.isPresent())
				toSend = optUser.get();
			else
				throw new ServiceException("Debe enviar los datos del usuario al que se deseas reenviar la invitación");
		}
		Optional<CodigoUsuario> existingUserCode = this.userCodesRepository.findByUserAndBusiness("" + toSend.getId(),
				"" + existingUE.getEmpresa().getId());
		if (existingUserCode.isPresent()) {
			CodigoUsuario codeToExpire = existingUserCode.get();
			codeToExpire.expirar();
			this.userCodesRepository.update(codeToExpire);
		}
		CodigoUsuario newUserCode = this.createUserCode(toSend.getId(), existingUE.getEmpresa().getId());
		this.userCodesRepository.save(newUserCode);
		this.emailHelper.SendInvitationEmail(toSend.getEmail(), existingUE.getEmpresa().getNombre(),
				newUserCode.getCodigo());
	}

	@Transactional
	@Override
	public void CancelInvitation(UsuarioPrincipal businessAdmin, Usuario toSend) throws ServiceException, Exception {
		UsuarioEmpresa existingUE = businessAdmin.getUsuarioEmpresa();
		if (toSend.getId() == null) {
			Optional<Usuario> optUser = this.userRepository.findByEmail(toSend.getEmail());
			if (optUser.isPresent())
				toSend = optUser.get();
			else
				throw new ServiceException("No existe un usuario con este mail");
		}

		Optional<CodigoUsuario> existingUserCode = this.userCodesRepository.findByUserAndBusiness("" + toSend.getId(),
				"" + existingUE.getEmpresa().getId());
		if (existingUserCode.isPresent()) {
			Usuario existingUser = this.userRepository.findById(toSend.getId());
			for (UsuarioEmpresa ue : existingUser.getUsuariosEmpresas()) {
				if (ue.getEmpresa().getId().equals(existingUE.getEmpresa().getId())) {
					ue.eliminar();
				}
			}
			this.userRepository.save(existingUser);

			CodigoUsuario codeToExpire = existingUserCode.get();
			codeToExpire.expirar();
			codeToExpire.eliminar();
			this.userCodesRepository.update(codeToExpire);
		} else
			throw new ServiceException("La invitación ya había sido cancelada");
	}

	@Override
	public void Modify(UsuarioPrincipal existingUser, Usuario usuario) throws ServiceException {
		Usuario existingDb = this.userRepository.findById(existingUser.getUsuario().getId());
		if (!usuario.getEmail().equals(existingDb.getEmail())) {
			Optional<Usuario> existingEmail = this.userRepository.findByEmail(usuario.getEmail());
			if (existingEmail.isPresent())
				throw new ServiceException("El email " + existingEmail.get().getEmail() + " ya existe");
		}

		if (usuario.getUsuario() != null && !usuario.getUsuario().equals(existingDb.getUsuario())) {
			Optional<Usuario> existingUsuario = this.userRepository.findByUsuario(usuario.getUsuario());
			if (existingUsuario.isPresent())
				throw new ServiceException("El Usuario " + existingUsuario.get().getUsuario() + " ya existe");
		}
		existingDb.setEmail(usuario.getEmail());
		if(usuario.getUsuario() != null)
		existingDb.setUsuario(usuario.getUsuario());
		existingDb.setNombre(usuario.getNombre());
		existingDb.setApellido(usuario.getApellido());
		this.userRepository.save(existingDb);
	}

	@Transactional
	@Override
	public void RemoveFromBusiness(UsuarioPrincipal existingUser, String id) throws ServiceException {
		try {
			Usuario toRemove = this.userRepository.findById(id);
			if (toRemove != null) {
				Company business = existingUser.getUsuarioEmpresa().getEmpresa();
				for (UsuarioEmpresa ue : toRemove.getUsuariosEmpresas()) {
					if (ue.getEmpresa().getId().equals(business.getId())) {
						ue.eliminar();
						ue.setValidado(false);
						ue.setActivo(false);
						ue.setRoles(null);
						usuarioEmpresaDAO.save(ue);
					}
				}
				this.emailHelper.SendRemoveFromBusiness(business, existingUser.getUsuario(), toRemove);
				this.userRepository.save(toRemove);
			} else
				throw new ServiceException("No existe usuario con este id");
		} catch (NumberFormatException ex) {
			throw new ServiceException("El id debe ser numérico");
		}

	}

	@Override
	public Usuario InviteAdmin(UsuarioPrincipal existing, Usuario newUser) throws ServiceException {
		newUser.setParametersForRegister();
		newUser.setEsAdministradorSistema(true);
		newUser.setValidado(false);

		Optional<Usuario> existingEmail = userRepository.findByEmail(newUser.getEmail());
		Optional<Usuario> existingUsername = newUser.getUsuario() != null ? userRepository.findByUsuario(newUser.getUsuario()) : Optional.empty();

		if (existingEmail.isPresent() || existingUsername.isPresent()) {
			throw new ServiceException("Ya hay un usuario con este email que no es administrador de sistema");
		}

		newUser = this.userRepository.save(newUser);
		CodigoUsuario newUserCode = this.createUserCode(newUser.getId(), "0");
		this.userCodesRepository.save(newUserCode);
		this.emailHelper.SendSystemAdminInvitation(newUser.getEmail(),
				existing.getUsuario().getNombre() + " " + existing.getUsuario().getApellido(), newUserCode.getCodigo());

		return newUser;
	}

	@Override
	public List<Usuario> GetSystemAdmins() {
		List<Usuario> users = this.userRepository.getSystemAdmins();
		for (Usuario u : users) {
			if (!u.getValidado()) {
				u.setValidadoParaEmpresa(false);
			} else
				u.setValidadoParaEmpresa(true);
		}
		return users;
	}

	@Override
	public void ReSendInvitationAdmin(UsuarioPrincipal systemAdmin, Usuario toSend) throws ServiceException, Exception {
		if (toSend.getId() == null) {
			Optional<Usuario> optToSend = this.userRepository.findByEmail(toSend.getEmail());
			if (optToSend.isPresent()) {
				toSend = optToSend.get();
			} else
				throw new ServiceException("No existe usuario con este email");
		}
		Optional<CodigoUsuario> existingUserCode = this.userCodesRepository.findByUserAndBusiness("" + toSend.getId(),
				"0");

		if (existingUserCode.isPresent()) {
			CodigoUsuario codeToExpire = existingUserCode.get();
			codeToExpire.expirar();
			this.userCodesRepository.update(codeToExpire);
		}
		CodigoUsuario newUserCode = this.createUserCode(toSend.getId(), "0");
		this.userCodesRepository.save(newUserCode);
		this.emailHelper.SendSystemAdminInvitation(toSend.getEmail(),
				systemAdmin.getUsuario().getNombre() + " " + systemAdmin.getUsuario().getApellido(),
				newUserCode.getCodigo());

	}

	@Override
	public void CancelInvitationAdmin(UsuarioPrincipal systemAdmin, Usuario toSend) throws ServiceException, Exception {
		if (toSend.getId() == null) {
			Optional<Usuario> optUser = this.userRepository.findByEmail(toSend.getEmail());
			if (optUser.isPresent())
				toSend = optUser.get();
			else
				throw new ServiceException("No existe un usuario con este mail");
		}
		Optional<CodigoUsuario> existingUserCode = this.userCodesRepository.findByUserAndBusiness("" + toSend.getId(),
				"0");

		if (existingUserCode.isPresent()) {
			Usuario existingUser = this.userRepository.findById(toSend.getId());
			existingUser.eliminar();

			this.userRepository.save(existingUser);

			CodigoUsuario codeToExpire = existingUserCode.get();
			codeToExpire.expirar();
			codeToExpire.eliminar();
			this.userCodesRepository.update(codeToExpire);
		} else
			throw new ServiceException("La invitación ya había sido cancelada");
	}

	@Override
	public void DeleteAdmin(String id) throws ServiceException {
		try {
			Usuario toDelete = this.userRepository.findById(id);
			if (toDelete != null) {
				if (toDelete.esAdministradorSistema()) {
					toDelete.eliminar();
					this.userRepository.save(toDelete);
				} else
					throw new ServiceException("Este usuario no es administrador");
			} else
				throw new ServiceException("No hay administrador para este id");
		} catch (NumberFormatException ex) {
			throw new ServiceException("El id del administrador a eliminar debe ser numérico");
		}

	}

}
