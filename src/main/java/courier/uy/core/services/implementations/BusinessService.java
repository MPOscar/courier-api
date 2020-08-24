package courier.uy.core.services.implementations;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import courier.uy.core.db.*;
import courier.uy.core.entity.*;
import courier.uy.core.services.interfaces.IBusinessService;
import courier.uy.core.auth.jwt.Roles;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.utils.IEmailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class BusinessService implements IBusinessService {

	@Autowired
	private EmpresasDAO businessRepository;
	@Autowired
	private UsuariosDAO userRepository;
	@Autowired
	private WishlistDAO wishlistRepository;
	@SuppressWarnings("unused")
	@Autowired
	private ProductosDAO productsRepository;
	@Autowired
	private CodigosUsuariosDAO userCodesRepository;
	@Autowired
	private RolesDAO rolesDAO;
	@Autowired
	private IEmailHelper emailHelper;
	@Autowired
	private BajasDAO inactivationsRepository;
	@Autowired
	private UsuarioEmpresaDAO usuarioEmpresaDAO;

	public BusinessService(EmpresasDAO businessRepo, UsuariosDAO userRepo, WishlistDAO wishlistRepo,
			ProductosDAO productsDAO, RolesDAO rolesDAO, CodigosUsuariosDAO userCodesDAO, IEmailHelper emailHelper,
			BajasDAO bajasDAO) {
		this.businessRepository = businessRepo;
		this.userRepository = userRepo;
		this.wishlistRepository = wishlistRepo;
		this.productsRepository = productsDAO;
		this.rolesDAO = rolesDAO;
		this.userCodesRepository = userCodesDAO;
		this.emailHelper = emailHelper;
		this.inactivationsRepository = bajasDAO;
	}

	@Transactional
	@Override
	public List<Company> GetUserBusinesses(UsuarioPrincipal user) throws ServiceException {
		return this.businessRepository.getEmpresasUsuario(user);
	}

	@Transactional
	@Override
	public Company Create(UsuarioPrincipal user, Company business) throws ServiceException {
		this.userRepository.existeUsuario(user.getUsuario());
		Usuario bddUser = this.userRepository.findById(user.getUsuario().getId());
		this.businessRepository.empresaNoEstaRepetida(business);
		if (user.getUsuario().getValidado() == true) {
			business.setValidado(true);
			UsuarioEmpresa ue = new UsuarioEmpresa();
			ue.setEmpresa(business);
			ue.setUsuario(bddUser);
			ue.setSusuario(bddUser.getId());
			ue.setActivo(true);
			ue.setValidado(true);
			Set<Rol> roles = new HashSet<Rol>();
			Optional<Rol> adminRole = this.rolesDAO.findByName(Roles.BUSINESS_ADMIN);
			if (adminRole.isPresent()) {
				roles.add(adminRole.get());
				ue.getSroles().add(adminRole.get().getId());
			}
			business.setGln("" + business.getRut());
			Company newBusiness = this.businessRepository.save(business);
			ue.setEmpresa(newBusiness);
			ue.setSempresa(newBusiness.getId());
			ue.setRoles(roles);
			ue.setRol(Roles.BUSINESS_ADMIN);
			usuarioEmpresaDAO.save(ue);
			bddUser.getUsuariosEmpresas().add(ue);
			bddUser.getSusuariosEmpresas().add(ue.getId());
			this.userRepository.save(bddUser);
			return business;

		} else
			throw new ServiceException("Solo los usuarios validados pueden crear empresas");
	}

	@Override
	public void UpdateSupermarket(UsuarioPrincipal user, Company business) throws ServiceException {
		this.userRepository.existeUsuario(user.getUsuario());
		this.businessRepository.empresaNoEstaRepetida(business);
		if (user.getUsuario().getValidado() == true && user.getUsuario().esAdministradorSistema()) {
			Company existing = this.businessRepository.findById(business.getId());
			existing.setGln(business.getGln());
			existing.setNombre(business.getNombre());
			existing.setRazonSocial(business.getRazonSocial());
			existing.setRut(business.getRut());
			this.businessRepository.update(existing);
		} else
			throw new ServiceException("Solo los administradores de sistema pueden modificar supermercados");
	}

	@Override
	public void Validate(String id) throws ServiceException {
		Company e = this.businessRepository.findById(id);
		if (e != null) {
			e.setValidado(true);
			this.businessRepository.update(e);
		} else
			throw new ServiceException("No existe empresa con el id " + id);

	}

	@Override
	public PaginadoResponse<List<Company>> GetBusinesses(PaginadoRequest pr, String idEmpresa, UsuarioPrincipal user)
			throws ServiceException {
		PaginadoResponse<List<Company>> response = null;
		if (user.getUsuarioEmpresa().getEmpresa() != null) {
			Company cliente = user.getUsuarioEmpresa().getEmpresa();
			try {
				response = this.businessRepository.getEmpresas(pr, idEmpresa);
			} catch (ModelException ex) {
				throw new ServiceException("Error al obtener las Empresas asociadas");
			}
			for (Company provider : response.getElementos()) {
				BigInteger cant = this.wishlistRepository.cantidadElementosWishlist(provider.getId(), cliente.getId());
				provider.setWishlistSize(cant.intValue());
			}
		}
		return response;
	}

	@Override
	public PaginadoResponse<List<Company>> GetBusinessesWithCatalogo(PaginadoRequest pr, String idEmpresa, UsuarioPrincipal user)
			throws ServiceException {
		PaginadoResponse<List<Company>> response = null;
		if (user.getUsuarioEmpresa().getEmpresa() != null) {
			Company cliente = user.getUsuarioEmpresa().getEmpresa();
			try {
				response = this.businessRepository.getEmpresasConCatalogo(pr, idEmpresa);
			} catch (ModelException ex) {
				throw new ServiceException("Error al obtener las Empresas asociadas");
			}
			for (Company provider : response.getElementos()) {
				BigInteger cant = this.wishlistRepository.cantidadElementosWishlist(provider.getId(), cliente.getId());
				provider.setWishlistSize(cant.intValue());
			}
		}
		return response;
	}

	@Override
	public PaginadoResponse<List<Company>> GetBusinessesWithVisibility(PaginadoRequest pr, String idEmpresa, UsuarioPrincipal user)
			throws ServiceException {
		PaginadoResponse<List<Company>> response = null;
		if (user.getUsuarioEmpresa().getEmpresa() != null) {
			Company cliente = user.getUsuarioEmpresa().getEmpresa();
			try {
				response = this.businessRepository.getEmpresasWithVisibility(pr, idEmpresa);
			} catch (ModelException ex) {
				throw new ServiceException("Error al obtener las Empresas asociadas");
			}
			for (Company provider : response.getElementos()) {
				BigInteger cant = this.wishlistRepository.cantidadElementosWishlist(provider.getId(), cliente.getId());
				provider.setWishlistSize(cant.intValue());
			}
		}
		return response;
	}

	@Override
	public PaginadoResponse<List<Company>> GetProveedores(PaginadoRequest pr, String idEmpresa, UsuarioPrincipal user)
			throws ServiceException {
		PaginadoResponse<List<Company>> response = null;
		if (user.getUsuarioEmpresa().getEmpresa() != null) {
			Company cliente = user.getUsuarioEmpresa().getEmpresa();
			try {
				response = this.businessRepository.getProveedoresEmpresa(pr, idEmpresa);
			} catch (ModelException ex) {
				throw new ServiceException("Error al obtener las Empresas asociadas");
			}
			for (Company provider : response.getElementos()) {
				BigInteger cant = this.wishlistRepository.cantidadElementosWishlist(provider.getId(), cliente.getId());
				provider.setWishlistSize(cant.intValue());
			}
		}
		return response;
	}

	@Override
	public void DeleteSupermarket(UsuarioPrincipal user, Company business) throws ServiceException {
		this.userRepository.existeUsuario(user.getUsuario());
		this.businessRepository.empresaNoEstaRepetida(business);
		if (user.getUsuario().getValidado() == true && user.getUsuario().esAdministradorSistema()) {
			Company existing = this.businessRepository.findById(business.getId());
			existing.eliminar();
			this.businessRepository.update(existing);
		} else
			throw new ServiceException("Solo los administradores de sistema pueden eliminar supermercados");

	}

	@Override
	public void Update(UsuarioPrincipal usuario, Company empresa) throws ServiceException {
		if (usuario.getUsuarioEmpresa().getEmpresa().getId() == empresa.getId()) {
			Company existing = this.businessRepository.findById(empresa.getId());
			if (!existing.getRut().equals(empresa.getRut())) {
				Optional<Company> existingRut = this.businessRepository.findByKey("rut", "" + empresa.getRut());
				if (existingRut.isPresent() && existingRut.get().getId() != empresa.getId())
					throw new ServiceException("El rut " + empresa.getRut() + " ya existe");

			}
			existing.setNombre(empresa.getNombre());
			existing.setRazonSocial(empresa.getRazonSocial());
			existing.setRut(empresa.getRut());
			existing.setFoto(empresa.getFoto());
			this.businessRepository.update(existing);
		}
	}

	private void sendRegisterEmail(Usuario usuario, Company business, String adminName) throws ServiceException {
		CodigoUsuario userCode = new CodigoUsuario(usuario);
		userCode.setEmpresa(business);
		this.userCodesRepository.save(userCode);
		this.emailHelper.SendRegisterFromAdminEmail(usuario.getEmail(), business.getNombre(), adminName,
				userCode.getCodigo());
	}

	private void sendNewBusinessEmail(Usuario usuario, Company business, String adminName) throws ServiceException {
		CodigoUsuario userCode = new CodigoUsuario(usuario);
		userCode.setEmpresa(business);
		this.userCodesRepository.save(userCode);
		this.emailHelper.SendNewBusinessFromAdminEmail(usuario.getEmail(), usuario.getNombre() + " " + usuario.getApellido(),
				business.getNombre(), adminName, userCode.getCodigo());
	}

	@Transactional
	@Override
	public void CreateFromAdmin(UsuarioPrincipal usuario, Company empresa) throws ServiceException, Exception {
		Usuario newUser;
		this.businessRepository.empresaNoEstaRepetida(empresa);
		this.businessRepository.save(empresa);
		Optional<Usuario> optUser = empresa.getEmail() != null ? this.userRepository.findByEmail(empresa.getEmail()) : Optional.empty();

		if (optUser.isPresent()) {
			newUser = optUser.get();
			this.sendNewBusinessEmail(newUser, empresa,
					usuario.getUsuario().getNombre() + " " + usuario.getUsuario().getApellido());
		} else {
			newUser = new Usuario();
			newUser.setParametersForRegister();
			newUser.setUsuario(empresa.getEmail());
			newUser.setEmail(empresa.getEmail());
			this.sendRegisterEmail(newUser, empresa,
					usuario.getUsuario().getNombre() + " " + usuario.getUsuario().getApellido());
			this.userRepository.save(newUser);
		}

		empresa.setValidado(true);
		empresa.setActivo(false);

		UsuarioEmpresa ue = new UsuarioEmpresa();
		ue.setEmpresa(empresa);
		ue.setUsuario(newUser);
		ue.setActivo(false);
		ue.setValidado(true);
		Set<Rol> roles = new HashSet<Rol>();
		Optional<Rol> adminRole = this.rolesDAO.findByName(Roles.BUSINESS_ADMIN);
		if (adminRole.isPresent()) {
			roles.add(adminRole.get());
		}

		ue.setRoles(roles);
		ue.setRol(Roles.BUSINESS_ADMIN);
		newUser.getUsuariosEmpresas().add(ue);

		this.userRepository.save(newUser);

	}

	@Override
	public List<Company> GetAll() throws ServiceException {
		return this.businessRepository.getAll();
	}

	@Override
	public void UpdateFromAdmin(UsuarioPrincipal usuario, Company empresa) throws ServiceException {
		Company existing = this.businessRepository.findById(empresa.getId());
		if (!existing.getRut().equals(empresa.getRut())) {
			Optional<Company> existingRut = this.businessRepository.findByKey("rut", "" + empresa.getRut());
			if (existingRut.isPresent() && existingRut.get().getId() != empresa.getId())
				throw new ServiceException("El rut " + empresa.getRut() + " ya existe");

		}
		existing.setNombre(empresa.getNombre());
		existing.setRazonSocial(empresa.getRazonSocial());
		existing.setRut(empresa.getRut());
		existing.setFoto(empresa.getFoto());

		this.businessRepository.update(existing);

	}

	@Transactional
	@Override
	public Company ActivateBusiness(UsuarioPrincipal usuario, Company empresa) throws ServiceException {
		Company existing = this.businessRepository.findById(empresa.getId());
		existing.setActivo(true);
		Baja lastExit = existing.getBaja();
		if(lastExit != null){
		lastExit.setActivo(false);
		lastExit.setFechaEdicion();
		this.inactivationsRepository.update(lastExit);
		}
		this.businessRepository.update(existing);
		return existing;
	}

	@Transactional
	@Override
	public Company DeactivateBusiness(UsuarioPrincipal usuario, Company empresa) throws ServiceException {
		Company existing = this.businessRepository.findById(empresa.getId());
		if (empresa.getNuevaBaja() != null) {
			Baja lastBaja = existing.getBaja();
			if (lastBaja != null) {
				lastBaja.setActivo(false);
				this.inactivationsRepository.update(lastBaja);
			}

			Baja newBaja = new Baja();
			newBaja.setActivo(true);
			newBaja.setAdmin(usuario.getUsuario());
			newBaja.setEmpresa(existing);
			newBaja.setMotivo(empresa.getNuevaBaja().getMotivo());
			this.inactivationsRepository.insert(newBaja);
			existing.getBajas().add(newBaja);
			existing.setActivo(false);
			this.businessRepository.update(existing);
		}

		return existing;

	}

	@Override
	public PaginadoResponse<List<Company>> getProviders(PaginadoRequest pr, UsuarioPrincipal usuario, Company empresa)
			throws ServiceException {
		if (empresa == null)
			return new PaginadoResponse<List<Company>>(1L, 0L, 0L, new LinkedList<Company>());
		try {
			return this.businessRepository.getProveedoresEmpresa(pr, empresa.getId());
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener los Proveedores: " + e.getMessage());
		}
	}

	@Override
	public PaginadoResponse<List<Company>> getEmpresasConProductosVisibles(PaginadoRequest pr, UsuarioPrincipal usuario, Company empresa)
			throws ServiceException {
		if (empresa == null)
			return new PaginadoResponse<List<Company>>(1L, 0L, 0L, new LinkedList<Company>());
		try {
			return this.businessRepository.getEmpresasConProductosVisibles(pr, empresa.getId());
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener los Proveedores: " + e.getMessage());
		}
	}

	@Transactional
	@Override
	public Optional<Company> obtenerEmpresa(String idEmpresa, UsuarioPrincipal usuario) throws ServiceException {
		return Optional.ofNullable(this.businessRepository.findById(idEmpresa));
	}

	@Transactional
	@Override
	public Set<Grupo> obtenerEmpresaGrupos(String idEmpresa, UsuarioPrincipal usuario) throws ServiceException {
		Company empresa = this.businessRepository.findById(idEmpresa);
		if (empresa == null) {
			throw new ServiceException("La Empresa de la que desea obtener los grupos no existe");
		}
		Set<Grupo> grupos = empresa.getGrupos();
		return grupos;
	}
}
