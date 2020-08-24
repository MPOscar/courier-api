package courier.uy.api.controller;

import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.ICategoryService;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
	Logger logger = LogManager.getLogger(CategoriaController.class);
	private final IAuthenticationFacade authenticationFacade;
	private final IErrorService errorService;
	private final ICategoryService categoryService;

	public CategoriaController(IAuthenticationFacade authenticationFacade,ICategoryService categoryService, IErrorService errorService) {
		this.authenticationFacade = authenticationFacade;
		this.categoryService = categoryService;
		this.errorService = errorService;
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@PostMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity post(@RequestBody Categoria category) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.categoryService.Create(category, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Categoria>(HttpStatus.OK.value(), category));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "categoria controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en categorias/post: " + ex.getMessage());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@PutMapping("/productos")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity setProducts(@RequestBody Categoria category) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.categoryService.ChangeProducts(category, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Set<Producto>>(HttpStatus.OK.value(), category.getProductos()));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @PutMapping(\"/productos\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "categoria controller @PutMapping(\"/productos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en categorias/changeProducts: " + ex.getMessage());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@GetMapping("")
	public ResponseEntity getCategorias(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			return ok(new Representacion<List<Categoria>>(HttpStatus.OK.value(),
					this.categoryService.GetAllFromBusiness(pr, usuario.getUsuarioEmpresa())));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity obtenerCategoriasProveedor(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit,
																@PathVariable("id") String providerId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			return ok(new Representacion<Categoria>(200,
					this.categoryService.GetCategoryById(providerId, usuario.getUsuarioEmpresa())));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @GetMapping(\"/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		}
	}

	@GetMapping("/empresa")
	public ResponseEntity obtenerCategoriasPorProveedorLogueado(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			UsuarioEmpresa ue = usuario.getUsuarioEmpresa();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			return ok(new Representacion<List<Categoria>>(HttpStatus.OK.value(),
					this.categoryService.GetAllFromProvider(pr, ue.getEmpresa().getId())));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @GetMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		}
	}

	@GetMapping("/empresa/{id}")
	public ResponseEntity obtenerCategoriasPorProveedor(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit,
			@PathVariable("id") String providerId) {
		try {
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			return ok(new Representacion<List<Categoria>>(HttpStatus.OK.value(),
					this.categoryService.GetAllFromProvider(pr, providerId)));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @GetMapping(\"/empresa/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		}
	}

	@GetMapping("/{id}/empresa")
	public ResponseEntity obtenerEmpresaCategoria(@PathVariable("id") String idCategoria) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Categoria cat = this.categoryService.GetCategoryById(idCategoria, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Company>(HttpStatus.OK.value(), cat.getEmpresa()));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @GetMapping(\"/{id}/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		}
	}

	@GetMapping("/{id}/padre")
	public ResponseEntity obtenerCategoriaPadre(@PathVariable("id") String idCategoria) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Categoria cat = this.categoryService.GetCategoryById(idCategoria, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Categoria>(HttpStatus.OK.value(), cat.getPadre()));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @GetMapping(\"/{id}/padre\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@DeleteMapping("/{categoryId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN)")
	public ResponseEntity eliminarCategoria(@PathVariable("categoryId") String categoryId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Categoria category = this.categoryService.GetCategoryById(categoryId);
			this.categoryService.Delete(category, usuario.getUsuarioEmpresa());
			return ok(new Representacion<String>(HttpStatus.OK.value(), "Categoría eliminada con éxito"));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "categoria controller @DeleteMapping(\"/{categoryId}\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "categoria controller @DeleteMapping(\"/{categoryId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en empresas/categorias: " + ex.getMessage());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

}