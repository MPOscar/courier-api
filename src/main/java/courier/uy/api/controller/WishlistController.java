package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import courier.uy.core.db.EmpaquesDAO;
import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.entity.Wishlist;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IWishlistService;
import courier.uy.core.utils.ExcelUtility;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.services.implementations.ProductsService;
import courier.uy.core.services.interfaces.IProductService;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
	Logger logger = LogManager.getLogger(WishlistController.class);
	private final IAuthenticationFacade authenticationFacade;

	private final IWishlistService wishlistService;
	private final IProductService productsService;

	private final IErrorService errorService;
	@Autowired
	private final EmpaquesDAO empaquesDAO;
	@Autowired
	private final EmpresasDAO empresasDAO;

	public WishlistController(IAuthenticationFacade authenticationFacad, IWishlistService wishlistService, IErrorService eService, EmpaquesDAO empaquesRepo,
                              ProductsService pService, EmpresasDAO empresasRepo) {
		this.authenticationFacade = authenticationFacad;
		this.wishlistService = wishlistService;
		this.errorService = eService;
		empaquesDAO = empaquesRepo;
		this.productsService = pService;
		this.empresasDAO = empresasRepo;
	}

	@GetMapping("/misProductos/{myBusinessId}/{formatBusinessId}")
	public ResponseEntity getMyProductsWithFormat(@PathVariable("myBusinessId") String myBusinessId,
												  @PathVariable("formatBusinessId") String formatBusinessId) {
		try {
			this.wishlistService.SetStrategy(formatBusinessId);
			Workbook wb = this.wishlistService.GetMyProductsWithFormat(myBusinessId);
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException {

					wb.write(out);
					out.flush();
				}
			};
			return ok(Response.ok(output).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.xls")
					.header(HttpHeaders.CONTENT_TYPE, "application/octet-stream").build());
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/misProductos/{myBusinessId}/{formatBusinessId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/misProductos/{myBusinessId}/{formatBusinessId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/misProductos/{myBusinessId}/{formatBusinessId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/misProductos/{myBusinessId}/{formatBusinessId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@GetMapping("/{providerId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity get(@PathVariable("providerId") String providerId) {
		try {
			UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
			List<Producto> allProducts = this.wishlistService.GetAllProductsFromProviderInWishlist(providerId,
					user.getUsuarioEmpresa());
			return ok(new Representacion<List<Producto>>(HttpStatus.OK.value(), allProducts));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@GetMapping("/tataIndividualWishlist/{providerId}")
	public ResponseEntity makeExcelForTata(@PathVariable("providerId") String providerId) {
		try {
			UsuarioEmpresa nuevo = new UsuarioEmpresa();
			Company business = this.empresasDAO.findById("9");
			nuevo.setEmpresa(business);
			List<Producto> allProducts = this.wishlistService.GetAllProductsFromProviderInWishlist(providerId, nuevo);
			Workbook wb = ExcelUtility.generateTataExcel(allProducts);
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException {

					wb.write(out);
					out.flush();
				}
			};
			DateTime dt = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm:ss");
			String dateFormated = fmt.print(dt);
			return ok(Response.ok(output)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Productos " + dateFormated + ".xls")
					.build());
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/tataIndividualWishlist/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/tataIndividualWishlist/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/tataIndividualWishlist/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/tataIndividualWishlist/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}


	@GetMapping("/tataIndividualFull/{providerId}")
	public ResponseEntity makeExcelForTataIndividualFull(@PathVariable("providerId") int providerId) {
		try {
			UsuarioEmpresa nuevo = new UsuarioEmpresa();
			Company business = this.empresasDAO.findById("9");
			nuevo.setEmpresa(business);
			Set<Producto> productos = this.productsService.obtenerProductosVisiblesEmpresa(nuevo, "" + providerId);
			List<Producto> allProducts = new ArrayList<Producto>();
			allProducts.addAll(productos);
			Workbook wb = ExcelUtility.generateTataExcel(allProducts);
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException {

					wb.write(out);
					out.flush();
				}
			};
			return ok(Response.ok(output).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productos.xls")
					.build());
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/tataIndividualFull/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller GetMapping(\"/tataIndividualFull/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/tataIndividualFull/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/tataIndividualFull/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.ADMIN })*/
	@GetMapping("/tataListWishlist/{providerId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity makeFullExcelForTata(@PathVariable("providerId") String providerId) {
		try {
			UsuarioEmpresa nuevo = new UsuarioEmpresa();
			Company business = this.empresasDAO.findById("9");
			nuevo.setEmpresa(business);
			List<Producto> allProducts = this.wishlistService.GetAllProductsFromProviderInWishlist(providerId, nuevo);

			Workbook wb = ExcelUtility.generateTataCompleteExcel(allProducts);
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException {
					wb.write(out);
					out.flush();
				}
			};
			return ok(Response.ok(output)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=listadoProductos.xls").build());
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/tataListWishlist/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/tataListWishlist/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"/tataListWishlist/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/tataListWishlist/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.ADMIN })*/
	@GetMapping("tataListFull/{providerId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity makeListExcelForTataFull(@PathVariable("providerId") int providerId) {
		try {
			UsuarioEmpresa nuevo = new UsuarioEmpresa();
			Company business = this.empresasDAO.findById("9");
			nuevo.setEmpresa(business);
			Set<Producto> productos = this.productsService.obtenerProductosVisiblesEmpresa(nuevo, "" + providerId);

			List<Producto> allProducts = new ArrayList<Producto>();
			allProducts.addAll(productos);
			Workbook wb = ExcelUtility.generateTataCompleteExcel(allProducts);
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException {

					wb.write(out);
					out.flush();
				}
			};
			return ok(Response.ok(output)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=listadoProductos.xls").build());
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"tataListFull/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"tataListFull/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @GetMapping(\"tataListFull/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"tataListFull/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@PostMapping("/{productId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity addToWishlist(@PathVariable("productId") String productId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Wishlist added = this.wishlistService.AddProductToWishlist(productId, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Wishlist>(HttpStatus.OK.value(), added));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @PostMapping(\"/{productId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/{productId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @PostMapping(\"/{productId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/{productId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@DeleteMapping("/{productId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity removeFromWishlist(@PathVariable("productId") String productId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.wishlistService.RemoveFromWishlist(productId, usuario.getUsuarioEmpresa());
			return ok(new Representacion<String>(HttpStatus.OK.value(), "Removed OK"));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @DeleteMapping(\"/{productId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/{productId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @DeleteMapping(\"/{productId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/{productId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN })*/
	@DeleteMapping("/clearForProvider/{providerId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity deleteAllFromProvider(@PathVariable("providerId") String providerId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			this.wishlistService.ClearWishlist(providerId, usuario.getUsuarioEmpresa());
			return ok(new Representacion<String>(HttpStatus.OK.value(), "Removed OK"));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "wishlist controller @DeleteMapping(\"/clearForProvider/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/clearForProvider/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "wishlist controller @DeleteMapping(\"/clearForProvider/{providerId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/clearForProvider/{providerId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

}
