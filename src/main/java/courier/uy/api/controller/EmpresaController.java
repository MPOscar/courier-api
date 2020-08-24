package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.entity.Grupo;
import courier.uy.core.services.interfaces.IProductService;
import courier.uy.core.services.interfaces.ISaleListService;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.Ubicacion;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.resources.dto.LoginResponse;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.resources.dto.UbicacionStatusResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.services.interfaces.IBusinessService;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IGroupService;
import courier.uy.core.services.interfaces.ILoginService;
import courier.uy.core.services.interfaces.IUbicacionService;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {
	Logger logger = LogManager.getLogger(EmpresaController.class);
	private final IAuthenticationFacade authenticationFacade;
	private final IErrorService errorService;
	private final IBusinessService empresasService;
	@SuppressWarnings("unused")
	private final IGroupService gruposService;
	private final ILoginService loginService;
	private final IUbicacionService ubicacionService;
	private final ISaleListService saleListService;
	private final IProductService productosService;

	public EmpresaController(IAuthenticationFacade authenticationFacade, IBusinessService empresasService, IGroupService gruposService, IErrorService errorService,
							ILoginService loginService, IUbicacionService ubicacionService, ISaleListService saleListService,
							IProductService productosService) {
		this.empresasService = empresasService;
		this.gruposService = gruposService;
		this.errorService = errorService;
		this.loginService = loginService;
		this.ubicacionService = ubicacionService;
		this.saleListService = saleListService;
		this.productosService = productosService;
		this.authenticationFacade = authenticationFacade;
	}

	@PostMapping("")
	public ResponseEntity create(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Company created = this.empresasService.Create(usuario, empresa);
			LoginResponse response = this.loginService.LoginBusiness(usuario, "" + created.getId());
			return ok(new Representacion<LoginResponse>(HttpStatus.OK.value(), response));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en empresas/post: " + ex.getMessage());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PostMapping("/admin")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity createFromAdmin(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.empresasService.CreateFromAdmin(usuario, empresa);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), empresa));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"/admin\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PostMapping(\"/admin\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"/admin\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PostMapping(\"/admin\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.SYSTEM_ADMIN, Roles.BUSINESS_ADMIN })*/
	@PutMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity update(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.empresasService.Update(usuario, empresa);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), empresa));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PutMapping("/admin")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity updateFromAdmin(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.empresasService.UpdateFromAdmin(usuario, empresa);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), empresa));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"/admin\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"/admin\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"/admin\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"/admin\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PutMapping("/alta")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity activate(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Company modified = this.empresasService.ActivateBusiness(usuario, empresa);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), modified));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"/alta\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"/alta\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"/alta\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"/alta\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PutMapping("/baja")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity deactivate(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Company modified = this.empresasService.DeactivateBusiness(usuario, empresa);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), modified));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"/baja\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"/baja\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PutMapping(\"/baja\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PutMapping(\"/baja\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.SYSTEM_ADMIN })*/
	@DeleteMapping("/supermercados")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity deleteSupermarket(@RequestBody Company empresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.empresasService.DeleteSupermarket(usuario, empresa);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), empresa));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @DeleteMapping(\"/supermercados\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @DeleteMapping(\"/supermercados\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @DeleteMapping(\"/supermercados\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @DeleteMapping(\"/supermercados\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
	@GetMapping("/usuario")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity getEmpresasUsuario() {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			List<Company> empresas = this.empresasService.GetUserBusinesses(usuario);
			return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), empresas));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/usuario\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/usuario\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/usuario\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/usuario\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@GetMapping("/administradores")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getAll() {
		try {
			List<Company> businesses = this.empresasService.GetAll();
			return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), businesses));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/administradores\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/administradores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/administradores\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/administradores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/{id}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity obtenerEmpresa(@PathVariable("id") String idEmpresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Optional<Company> empresa = this.empresasService.obtenerEmpresa(idEmpresa, usuario);
			return ok(new Representacion<Company>(HttpStatus.OK.value(), empresa.orElse(null)));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/{idEmpresa}/grupos")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity obtenerGruposEmpresa(@PathVariable("idEmpresa") String idEmpresa) {
		// FIXME Implementar método de paginación para esta colección
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			if (!usuario.getUsuarioEmpresa().getEmpresa().getId().equals(idEmpresa)
					&& !usuario.getUsuario().esAdministradorSistema())
				throw new ServiceException("Solo puede obtener los Grupos de la Empresa actualmente logueda");
			Set<Grupo> grupos = this.empresasService.obtenerEmpresaGrupos(idEmpresa, usuario);
			return ok(new Representacion<Set<Grupo>>(HttpStatus.OK.value(), grupos));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/grupos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/grupos\" Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/grupos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/grupos\" Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@GetMapping("/{idEmpresa}/listas")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity obtenerListasEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("idEmpresa") String idEmpresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<ListaDeVenta>> listaVentaResponse = this.saleListService
					.GetAllFromProviderForSupermarket(pr, usuario, idEmpresa);
			listaVentaResponse.inicializarLink("/" + idEmpresa + "/listas");
			Representacion<List<ListaDeVenta>> response = new Representacion<List<ListaDeVenta>>(HttpStatus.OK.value(),
					listaVentaResponse);
			return ok(response);
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/listas\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/listas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/listas\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/listas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
	@GetMapping("/{idEmpresa}/productos")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity obtenerProductosEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit,
																  @PathVariable("idEmpresa") String idEmpresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<Producto>> productosVisibles = this.productosService
					.obtenerProductosVisiblesEmpresa(pr, usuario.getUsuarioEmpresa(), idEmpresa);

			productosVisibles.inicializarLink("/empresas/" + idEmpresa + "/productos");
			Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
					productosVisibles);

			return ok(response);
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/productos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/productos\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/productos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/productos\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
	@GetMapping("/{idEmpresa}/productos-visibles")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity obtenerProductosVisiblesParaEmpresaSeleccionada(
			@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("idEmpresa") String idEmpresa) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<Producto>> productosVisibles = this.productosService
					.obtenerProductosVisiblesParaEmpresaSeleccionada(pr, usuario.getUsuarioEmpresa(), idEmpresa);

			productosVisibles.inicializarLink("/empresas/" + idEmpresa + "/productos");
			Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
					productosVisibles);

			return ok(response);
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/productos-visibles\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/productos-visibles\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/productos-visibles\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/productos-visibles\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
	@GetMapping("/{idEmpresa}/productos/{idProducto}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity getSingleProduct(@PathVariable("idEmpresa") String businessId, @PathVariable("idProducto") String productId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Producto producto = productosService.GetSingleProduct(usuario.getUsuarioEmpresa(), businessId, productId);
			return ok(new Representacion<Producto>(HttpStatus.OK.value(), producto));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/productos/{idProducto}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/productos/{idProducto}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/{idEmpresa}/productos/{idProducto}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/{idEmpresa}/productos/{idProducto}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/con-visibilidad")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getEmpresasConVisivilidad(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<Company>> empresasProveedoras = this.empresasService.GetBusinessesWithVisibility(pr,
					usuario.getUsuarioEmpresa().getEmpresa().getId(), usuario);
			empresasProveedoras.inicializarLink("/empresas");

			return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), empresasProveedoras));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/con-visibilidad\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/con-visibilidad\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/con-visibilidad\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/con-visibilidad\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN})*/
	@GetMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getEmpresas(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);

			if (usuario.getUsuarioEmpresa() == null) {
				List<Company> empresasProveedoras = new ArrayList<>();
				return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), empresasProveedoras));
			}

			PaginadoResponse<List<Company>> empresasProveedoras = this.empresasService.GetBusinesses(pr,
					usuario.getUsuarioEmpresa().getEmpresa().getId(), usuario);
			empresasProveedoras.inicializarLink("/empresas");

			return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), empresasProveedoras));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/catalogo")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getEmpresasCatalogo(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<Company>> empresasProveedoras = this.empresasService.GetBusinessesWithCatalogo(pr,
					usuario.getUsuarioEmpresa().getEmpresa().getId(), usuario);
			empresasProveedoras.inicializarLink("/empresas");

			return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), empresasProveedoras));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/catalogo\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/catalogo\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/catalogo\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/catalogo\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/proveedores")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getProveedoresEmpresas(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<Company>> empresasProveedoras = this.empresasService.GetProveedores(pr,
					usuario.getUsuarioEmpresa().getEmpresa().getId(), usuario);
			empresasProveedoras.inicializarLink("/empresas/providers");

			return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), empresasProveedoras));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/proveedores\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/proveedores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/proveedores\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/proveedores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.SYSTEM_ADMIN })*/
	@PostMapping("validar/{id}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity validarEmpresa(@PathVariable("id") String id) {
		try {
			this.empresasService.Validate(id);
			return ok(new Representacion<String>(HttpStatus.OK.value(), "Empresa validada con éxito"));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"validar/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PostMapping(\"validar/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"validar/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PostMapping(\"validar/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/**
	 * Se agregan las Ubicaciones pasadas en el Body y se devuelve un listado de los
	 * Status de estas Ubicaciones con un mensaje
	 *
	 * @param "user"
	 * @param ubicaciones
	 * @return {@link UbicacionStatusResponse}: notificación del estado de la
	 *         petición realizada
	 */
	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.BUSINESS_ADMIN, })*/
	@PostMapping("/ubicaciones")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity agregarUbicaciones(@RequestBody List<Ubicacion> ubicaciones) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			List<UbicacionStatusResponse> ubicacionStatusResponses = new ArrayList<UbicacionStatusResponse>();
			UsuarioEmpresa ue = usuario.getUsuarioEmpresa();
			for (Ubicacion ubi : ubicaciones) {
				String mensaje = "Ubicación agregada o actualizada correctamente";
				boolean creado = true;
				Ubicacion ubicacionCreada = null;
				if (ubi.getEmpresa() == null) {
					ubi.setEmpresa(ue.getEmpresa());
				}
				try {
					ubicacionCreada = this.ubicacionService.upsert(ubi, ue);
					ubicacionStatusResponses.add(new UbicacionStatusResponse(creado, ubicacionCreada, mensaje));
				} catch (Exception e) {
					creado = false;
					mensaje = "Ubicación no fué agregada o actualizada. Error: " + e.getMessage();
					ubicacionStatusResponses.add(new UbicacionStatusResponse(creado, ubi, mensaje));
				}
			}

			Representacion<List<UbicacionStatusResponse>> result = new Representacion<List<UbicacionStatusResponse>>(
					HttpStatus.OK.value(), ubicacionStatusResponses);
			return ok(result);
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @PostMapping(\"/ubicaciones\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @PostMapping(\"/ubicaciones\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/**
	 * Se obtienen las {@link Ubicacion} asociadas a la {@link Company} activa en el
	 * Token asociado al objeto {@link UsuarioEmpresa} actual.
	 *
	 * @param "user"
	 * @return {@link List}<{@link Ubicacion}>: Listado de las {@link Ubicacion}
	 *         encontradas.
	 */
	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/ubicaciones")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getUbicaciones() {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			List<Ubicacion> ubicaciones = this.ubicacionService.getAll(null, usuario.getUsuarioEmpresa());

			return ok(new Representacion<List<Ubicacion>>(HttpStatus.OK.value(), ubicaciones));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/ubicaciones\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/ubicaciones\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @GetMapping(\"/ubicaciones\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @GetMapping(\"/ubicaciones\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/**
	 * Elimina las Ubicaciones correspondientes a los códigos pasados por parámetros
	 * en forma de listado en el Body.
	 *
	 * @param codigoUbicaciones
	 * @param "usuario"
	 * @return {@link List}<{@link Ubicacion}>: Listado de las ubicaciones
	 *         eliminadas
	 */
	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@DeleteMapping("/ubicaciones")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public Representacion<List<Ubicacion>> deleteUbicaciones(@RequestBody List<String> codigoUbicaciones) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			List<Ubicacion> ubicacionesEliminadas = new ArrayList<Ubicacion>();
			for (String codigo : codigoUbicaciones) {
				Ubicacion ubicacion = new Ubicacion();
				ubicacion.setCodigo(codigo);
				ubicacion = this.ubicacionService.delete(ubicacion, usuario.getUsuarioEmpresa());
				ubicacionesEliminadas.add(ubicacion);
			}
			return new Representacion<List<Ubicacion>>(HttpStatus.OK.value(), ubicacionesEliminadas);
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empresa controller @DeleteMapping(\"/ubicaciones\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @DeleteMapping(\"/ubicaciones\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empresa controller @DeleteMapping(\"/ubicaciones\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empresa controller @DeleteMapping(\"/ubicaciones\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
}