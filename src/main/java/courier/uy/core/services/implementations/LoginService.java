package courier.uy.core.services.implementations;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import courier.uy.core.resources.dto.UsuarioBasic;
import courier.uy.core.services.interfaces.ILoginService;
import com.google.common.hash.Hashing;

import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.db.ParamsDAO;
import courier.uy.core.db.UsuarioEmpresaDAO;
import courier.uy.core.db.UsuariosDAO;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.LoginResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Param;
import courier.uy.core.entity.Rol;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements ILoginService {
	@Autowired
	private ParamsDAO paramsDAO;
	@Autowired
	private UsuariosDAO usuariosDAO;
	@Autowired
	private UsuarioEmpresaDAO usuarioEmpresaDAO;
    @Autowired
	private EmpresasDAO empresasDAO;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
    JwtTokenProvider jwtTokenProvider;

	@Override
	public LoginResponse LoginBasic(UsuarioBasic usuario) throws ServiceException, Exception {
		Usuario u = authenticate(usuario.getUsuario(), usuario.getContrasena());
		Optional<String> opt = Optional.empty();
		LoginResponse loginResponse = this.Login(u, opt);
		return loginResponse;
	}

	@Override
	public LoginResponse Login(Usuario usuario, Optional<String> id) throws ServiceException, Exception {
		Optional<Usuario> optUsuario = Optional.empty();
		if(usuario.getUsuario() != null) {
			optUsuario = this.usuariosDAO.findByUsuario(usuario.getUsuario());
		}
		else if (!optUsuario.isPresent() && usuario.getEmail() != null) {
			optUsuario = this.usuariosDAO.findByEmail(usuario.getEmail());
		}else{
			throw new ServiceException("No hay usuario con este nombre");
		}
		if (optUsuario.isPresent()) {
			Company emp = null;
			Usuario u = optUsuario.get();
			Param jwtKey = paramsDAO.findByNombre("JWT_SECRET_KEY");
			Param expTime = paramsDAO.findByNombre("TOKEN_EXP_TIME");
			List<String> allRoles = new ArrayList<String>();
			JsonWebSignature jws;
			Optional<UsuarioEmpresa> usuEmp = this.usuarioEmpresaDAO.obtenerUsuarioEmpresa(usuario.getId(),
					id.isPresent() ? id.get() : null, true, true);
			if (usuEmp.isPresent()) {

				if (usuEmp != null) {
					emp = usuEmp.get().getEmpresa();
					for (Rol rol : usuEmp.get().getRoles()) {
						allRoles.add(rol.getRol());
					}
				}

				jws = jwtTokenProvider.buildToken(u, usuEmp.get(), jwtKey, Float.parseFloat(expTime.getValor()));
			} else {
				jws = jwtTokenProvider.buildToken(u, null, jwtKey, Float.parseFloat(expTime.getValor()));
			}

			String jwt = jws.getCompactSerialization();

			JsonWebEncryption jwe = jwtTokenProvider.encryptToken(jwt, jwtKey);

			String token = jwe.getCompactSerialization();

			if (u.esAdministradorSistema() != null && u.esAdministradorSistema())
				allRoles.add("systemAdmin");
			Set<Company> businesses = this.empresasDAO.obtenerEmpresasAsociadasUsuario(u);
			return new LoginResponse(token, allRoles, u, businesses, emp);
		} else {
			throw new ServiceException("No hay usuario con este nombre");
		}

	}

	private Usuario authenticate(String username, String password) throws ServiceException {
		Optional<Usuario> result;
		try {
			result = usuariosDAO.findByUsuario(username);
			if (!result.isPresent())
				result = usuariosDAO.findByEmail(username);

			if (result.isPresent() && result.get().getActivo() && result.get().getValidado()) {
				String sha256hex = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
				if (sha256hex.toUpperCase().equals((result.get().getContrasena().toUpperCase()))) {
					authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
					return result.get();
				}
				throw new ServiceException("Contraseña incorrecta para usuario");
			}

			throw new ServiceException("Este usuario no está validado");
		} catch (Exception e) {
			throw new ServiceException("Ingreso no autorizado. Mensaje " + e.getMessage());
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

	@Override
	public LoginResponse LoginBusiness(UsuarioPrincipal user, String id) throws ServiceException, Exception {
		Optional<String> optId = Optional.of(id);
		return this.Login(user.getUsuario(), optId);
	}

}
