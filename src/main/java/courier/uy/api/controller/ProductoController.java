package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IBusinessService;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.utils.ExcelUtility;
import courier.uy.core.utils.IEmailHelper;
import courier.uy.core.utils.mapstruct.Cloner;
import courier.uy.core.db.ProductosDAO;
import courier.uy.core.entity.Grupo;
import courier.uy.core.resources.dto.*;
import courier.uy.core.services.interfaces.IProductService;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import courier.uy.core.db.CategoriasDAO;
import courier.uy.core.db.EmpaquesDAO;
import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.db.ParamsDAO;
import courier.uy.core.db.PresentacionesDAO;
import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Param;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.ProductoAccion;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.UsuarioEmpresa;

@RestController
@RequestMapping("/productos")
public class ProductoController {
  Logger logger = LogManager.getLogger(ProductoController.class);
  private final IProductService productosService;
  private final ProductosDAO productosDAO;
  private final IErrorService errorService;
  @SuppressWarnings("unused")
  private final CategoriasDAO categoriasDAO;
  @SuppressWarnings("unused")
  private final PresentacionesDAO presentacionesDAO;
  @SuppressWarnings("unused")
  private final EmpaquesDAO empaquesDAO;
  private final ParamsDAO paramsDAO;
  private final EmpresasDAO empresasDAO;
  private final IBusinessService businessService;

  @Value("${bucket:rondanet}")
  private final String bucket = "rondanet";

  private IEmailHelper emailHelper;

  private final IAuthenticationFacade authenticationFacade;

  public ProductoController(IProductService productosService, IBusinessService businessService,
                            IErrorService errorService, ProductosDAO productosDAO, CategoriasDAO categoriasDAO,
                            PresentacionesDAO presentacionesDAO, EmpaquesDAO empaquesDAO, ParamsDAO paramsDAO, EmpresasDAO empresasDAO,
                            IEmailHelper emailHelper, IAuthenticationFacade authenticationFacade) {
    this.productosService = productosService;
    this.errorService = errorService;
    this.productosDAO = productosDAO;
    this.categoriasDAO = categoriasDAO;
    this.presentacionesDAO = presentacionesDAO;
    this.empaquesDAO = empaquesDAO;
    this.paramsDAO = paramsDAO;
    this.empresasDAO = empresasDAO;
    this.businessService = businessService;
    this.emailHelper = emailHelper;
    this.authenticationFacade = authenticationFacade;
  }

  //@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })
  @GetMapping("")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerProductosVisibleEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest pr = new PaginadoRequest(page, limit);
      Company empresa = user.getUsuarioEmpresa().getEmpresa();
      PaginadoResponse<List<Producto>> productosResponse = this.productosService.obtenerProductosVisiblesEmpresa(pr,
          user.getUsuarioEmpresa(), empresa.getId());
      productosResponse.inicializarLink("/productos");
      Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
          productosResponse);
      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/empresa/productosVisibles/{id}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerProductosVisiblePorEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("id") Long id) {
    try {
      PaginadoRequest pr = new PaginadoRequest(page, limit);
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Company empresa = user.getUsuarioEmpresa().getEmpresa();
      PaginadoResponse<List<Producto>> productosResponse = this.productosService.obtenerProductosVisiblesEmpresa(pr,
          user.getUsuarioEmpresa(), empresa.getId());

      productosResponse.inicializarLink("/productos");
      Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
          productosResponse);

      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa/productosVisibles/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa/productosVisibles/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa/productosVisibles/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa/productosVisibles/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/empresa")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerProductosEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @RequestParam(defaultValue = "") List<String> filters,
      @RequestParam(defaultValue = "") List<String> marcas, @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones, @RequestParam(defaultValue = "description") String sort) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      paginadoRequest.setSort(sort);
      Company empresa = new Company();
      if (user.getUsuarioEmpresa() == null) {
        List<Producto> productos = new ArrayList<>();
        return ok(new Representacion<List<Producto>>(HttpStatus.OK.value(), productos));
      }
      PaginadoResponse<List<Producto>> productosResponse = this.productosService
          .obtenerProductosEmpresa(paginadoRequest, user.getUsuarioEmpresa(), empresa.getId());

      productosResponse.inicializarLink("/productos");
      Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
          productosResponse);

      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }


  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/empresa/{id}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getProductsFromBusiness(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit,
      @RequestParam(defaultValue = "") List<String> filters, @RequestParam(defaultValue = "") List<String> marcas,
      @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones,
      @RequestParam(defaultValue = "description") String sort, @PathVariable("id") String id) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      PaginadoResponse<List<Producto>> productosResponse = productosService
          .obtenerProductosVisiblesEmpresa(paginadoRequest, user.getUsuarioEmpresa(), id);
      Representacion<List<Producto>> response = new Representacion<List<Producto>>(
          HttpStatus.OK.value(), productosResponse);

      return ok(response);
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/empresa/filtros/{id}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerProductosEmpresaFiltros(
          @RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit,
      @PathVariable("id") String id) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      String empresaId = !id.equals("0") ? id : user.getUsuarioEmpresa().getEmpresa().getId();
      FiltrosResponse filtrosResponse = new FiltrosResponse();

      List<Set<String>> filtros = this.productosService.obtenerProductosEmpresaProveedorFiltros(paginadoRequest,
              user.getUsuarioEmpresa(), empresaId);

      filtrosResponse.setMarcas(filtros.get(0));
      filtrosResponse.setLineas(filtros.get(1));
      filtrosResponse.setDivisiones(filtros.get(2));

      Representacion<FiltrosResponse> response = new Representacion<FiltrosResponse>(HttpStatus.OK.value(),
          filtrosResponse);

      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa/filtros/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa/filtros/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa/filtros/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa/filtros/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/empresa/listaVenta/{empresaId}/{listaVentaId}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerProductosEmpresaListaVenta(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @RequestParam(defaultValue = "") List<String> filters,
                                                @RequestParam(defaultValue = "") List<String> marcas, @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones, @RequestParam(defaultValue = "description") String sort,
                                                          @PathVariable("id") Long empresaId, @PathVariable("id") Long listaVentaId) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      paginadoRequest.setSort(sort);
      Company empresa = new Company();
      if (user.getUsuarioEmpresa() == null) {
        List<Producto> productos = new ArrayList<>();
        return ok(new Representacion<List<Producto>>(HttpStatus.OK.value(), productos));
      }
      PaginadoResponse<List<Producto>> productosResponse = this.productosService
              .obtenerProductosEmpresa(paginadoRequest, user.getUsuarioEmpresa(), empresa.getId());

      productosResponse.inicializarLink("/productos");
      List<Long> productosEmpresa = new ArrayList<>();
      Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
              productosResponse);

      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresa\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @GetMapping("/visibles/{id}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getVisibleProducts(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("id") String id) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest pr = new PaginadoRequest(page, limit);
      PaginadoResponse<List<Producto>> productosResponse = productosService.GetVisibleForBusiness(pr, id,
          user.getUsuarioEmpresa());
      productosResponse.inicializarLink("/productos");
      Representacion<List<Producto>> response = new Representacion<List<Producto>>(HttpStatus.OK.value(),
          productosResponse);
      return ok(response);
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/visibles/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/visibles/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity deleteProduct(@PathVariable("id") String id) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      this.productosService.Delete(id, user);
      return ok(new Representacion<String>(HttpStatus.OK.value(), "Producto eliminado con éxito"));
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @DeleteMapping(\"/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @DeleteMapping(\"/{id}\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @DeleteMapping("/delete")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity deleteProductsArray(@RequestBody EliminarProductos eliminarProductos) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      this.productosService.deleteProductsArray(eliminarProductos, user);
      return ok(new Representacion<String>(HttpStatus.OK.value(), "Productos eliminados con éxito"));
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @DeleteMapping(\"/delete\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @DeleteMapping(\"/delete\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/visibles/{idProducto}/grupos")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerGruposConVisibilidad(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("idProducto") String idProducto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      UsuarioEmpresa ue = user.getUsuarioEmpresa();
      PaginadoRequest pr = new PaginadoRequest(page, limit);
      PaginadoResponse<List<Grupo>> empaquesResponse = this.productosService.obtenerGruposConVisibilidad(pr, idProducto,
          ue);
      empaquesResponse.inicializarLink("/productos/visibles/" + idProducto + "/grupos");
      Representacion<List<Grupo>> response = new Representacion<List<Grupo>>(HttpStatus.OK.value(), empaquesResponse);

      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/visibles/{idProducto}/grupos\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/visibles/{idProducto}/grupos\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/visibles/{idProducto}/grupos\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/visibles/{idProducto}/grupos\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/visibles/{idProducto}/empresas")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerEmpresasConVisibilidad(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("idProducto") String idProducto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      UsuarioEmpresa ue = user.getUsuarioEmpresa();
      PaginadoRequest pr = new PaginadoRequest(page, limit);
      PaginadoResponse<Set<Company>> empaquesResponse = this.productosService.obtenerEmpresasConVisibilidad(pr,
          idProducto, ue);
      empaquesResponse.inicializarLink("/productos/visibles/" + idProducto + "/empresas");
      Representacion<Set<Company>> response = new Representacion<Set<Company>>(HttpStatus.OK.value(), empaquesResponse);

      return ok(response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/visibles/{idProducto}/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/visibles/{idProducto}/empresas\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/visibles/{idProducto}/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/visibles/{idProducto}/empresas\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  private void updateGtins() {
    List<Producto> products = this.productosDAO.getAll();
    for (Producto p : products) {
      long noZero = Long.parseLong(p.getGtin());
      p.setGtin("" + noZero);
      this.productosDAO.update(p);
    }
  }

  /*@RolesAllowed({ Roles.SYSTEM_ADMIN })*/
  @GetMapping("/updateFiles")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity updateFiles() {
    try {
      this.updateGtins();
      return ok(new Representacion<String>(HttpStatus.OK.value(),
          this.productosService.actualizarURLImagenes(this.bucket, null)));
    } catch (AmazonServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/updateFiles\") Error:", ex.getMessage(), ex.getStackTrace());
      // The call was transmitted successfully, but Amazon S3 couldn't process
      // it, so it returned an error response.
      ex.printStackTrace();
      return ok(new Representacion<String>(HttpStatus.OK.value(), "1" + ex.getStackTrace()));

    } catch (SdkClientException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/updateFiles\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/updateFiles\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      // Amazon S3 couldn't be contacted for a response, or the client
      // couldn't parse the response from Amazon S3.
      ex.printStackTrace();
      return ok(new Representacion<String>(HttpStatus.OK.value(), "2" + ex.getStackTrace()));

    }

  }

  /*@RolesAllowed({ Roles.NEW_USER })*/
  @GetMapping("/getAccessKey")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).NEW_USER)")
  public ResponseEntity getAccessKey() {
    Param key = paramsDAO.findByNombre("s3_key");
    Param keyId = paramsDAO.findByNombre("s3_id");
    AccessKeyModel keyModel = new AccessKeyModel(key.getValor(), keyId.getValor());
    return ok(new Representacion<AccessKeyModel>(HttpStatus.OK.value(), keyModel));
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PutMapping("/visibilidadMultiple")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity cambiarVisibilidadMultiple(@RequestBody VisibilityRequest visibilityRequest) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      this.productosService.ChangeMassiveVisibility(visibilityRequest, user.getUsuarioEmpresa());
      return ok(new Representacion<String>(HttpStatus.OK.value(), "Visibilidad masiva cambiada con éxito"));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/visibilidadMultiple\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/visibilidadMultiple\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/visibilidadMultiple\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/visibilidadMultiple\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PutMapping("/visibilidadEmpresa")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity cambiarVisibilidadEmpresa(@RequestBody VisibilityRequest visibilityRequest) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      this.productosService.ChangeBusinessVisibility(visibilityRequest, user.getUsuarioEmpresa());
      return ok(new Representacion<String>(HttpStatus.OK.value(), "Visibilidad de empresa cambiada con éxito"));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/visibilidadEmpresa\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/visibilidadEmpresa\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/visibilidadEmpresa\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/visibilidadEmpresa\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PutMapping("/visibilidad")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity cambiarVisibilidad(@RequestBody Producto producto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      this.productosService.ChangeVisibility(producto, user.getUsuarioEmpresa());
      return ok(new Representacion<String>(HttpStatus.OK.value(), "Visibilidad cambiada con éxito"));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/visibilidad\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/visibilidad\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/visibilidad\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/visibilidad\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PostMapping("")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity crearProducto(@RequestBody Producto producto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Producto creado = this.productosService.Insert(producto, user);
      return ok(new Representacion<Producto>(HttpStatus.OK.value(), creado));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PostMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PostMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PostMapping("/excel")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity crearProductosExcel(@RequestParam("file") MultipartFile file,
      @RequestParam(defaultValue = "false") Boolean actualizarExistentes, @RequestParam(defaultValue = "false") Boolean eliminarExistentes, @RequestParam(defaultValue = "false") Boolean eliminarTodos) {
    try {
      //InputStream is = new BufferedInputStream(file.getInputStream());
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      InputStream is = file.getInputStream();
      List<ExcelProduct> productList = ExcelUtility.excelToProductList(is, user.getUsuarioEmpresa().getEmpresa());
      this.productosService.InsertExcel(productList, user, actualizarExistentes, eliminarExistentes, eliminarTodos);

      Workbook errors = this.productosService.getErrorExcel(productList);

      String excelUrl = this.productosService.uploadErrorExcel(errors, user.getUsuarioEmpresa().getEmpresa().getRut(),
          user.getUsuarioEmpresa().getId(), false);
      int productsLoaded = 0;
      int productsWithErrors = 0;
      for (ExcelProduct p : productList) {
        if (p.getWasCreated())
          productsLoaded++;
        if (p.getHasErrors() || !p.getWasCreated())
          productsWithErrors++;
      }
      String opcion = "cargaron";
      if (eliminarExistentes) {
        opcion = "eliminaron";
      } else if (actualizarExistentes) {
        opcion = "actualizaron";
      }
      String returnString = "Se " + opcion + " " + productsLoaded + " productos con éxito.";
      if (productsWithErrors > 1) {
        returnString += " * " + excelUrl;
        this.emailHelper.SendExcelErrorsEmail(user.getUsuario().getEmail(),
            user.getUsuarioEmpresa().getEmpresa().getNombre(), excelUrl);
        // send email;
      }

      return ok(new Representacion<String>(HttpStatus.OK.value(), returnString));

    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PostMapping(\"/excel\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PostMapping(\"/excel\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PostMapping(\"/excel\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PostMapping(\"/excel\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/exportar")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity exportarProductosExcel(@RequestParam(defaultValue = "1") Long page,
      @RequestParam(defaultValue = "10000") Long limit, @RequestParam(defaultValue = "") List<String> filters,
      @RequestParam(defaultValue = "") List<String> marcas, @RequestParam(defaultValue = "") List<String> lineas,
      @RequestParam(defaultValue = "") List<String> divisiones, @RequestParam(defaultValue = "description") String sort) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      Company empresa = user.getUsuarioEmpresa().getEmpresa();
      String string = this.productosService.exportarExcel(paginadoRequest, user.getUsuarioEmpresa());

      Representacion<String> response = new Representacion<String>(HttpStatus.OK.value(), string);

      return ok(response);
      /*
       * } catch (ServiceException ex) { throw new
       * WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
       */
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/exportar\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/exportar\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PutMapping("")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity editProducto(@RequestBody Producto producto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Producto creado = this.productosService.Modify(producto, user);
      return ok(new Representacion<Producto>(HttpStatus.OK.value(), creado));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*****************************************************************************************************************
   * 
   * INICIO ZONA API ACCIONES
   * 
   *****************************************************************************************************************/

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @GetMapping("/acciones")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getNotAcknowledgedActions(
      @RequestParam(defaultValue = "0") String business, @RequestParam(defaultValue = "0") String provider) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      UsuarioEmpresa ue = user.getUsuarioEmpresa();
      List<ProductoAccion> productActions = this.productosService.GetNotAcknowledgedActions(business, provider, ue);
      return ok(new Representacion<List<ProductoAccion>>(HttpStatus.OK.value(), productActions));
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/acciones\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/acciones\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_PRODUCTS })*/
  @PutMapping("/acciones/recibirAcusacion")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_PRODUCTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity setAcknowledge(@RequestBody AcknowledgeRequest acknowledgeRequest) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      UsuarioEmpresa ue = user.getUsuarioEmpresa();
      Usuario usuario = ue.getUsuario();
      if (!usuario.esAdministradorSistema()) {
        acknowledgeRequest.setIdEmpresa(ue.getEmpresa().getId());
      }
      this.productosService.SetAcknowledge(acknowledgeRequest, ue);
      return ok(new Representacion<String>(HttpStatus.OK.value(), "ok"));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/acciones/recibirAcusacion\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/acciones/recibirAcusacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @PutMapping(\"/acciones/recibirAcusacion\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @PutMapping(\"/acciones/recibirAcusacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/{a:empresas|empresa}/{idEmpresa}/{idProducto}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getSingleProduct( @PathVariable("idEmpresa") String idEmpresa, @PathVariable("idProducto") String idProducto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Producto producto = productosService.GetSingleProduct(user.getUsuarioEmpresa(), idEmpresa, idProducto);
      ProductoAccionesResponse productoAcciones = Cloner.MAPPER.clone(producto);

      return ok(new Representacion<ProductoAccionesResponse>(HttpStatus.OK.value(), productoAcciones));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/{a:empresas|empresa}/{idEmpresa}/{idProducto}/visibilidad")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getSingleProductVisibilidad( @PathVariable("idEmpresa") String idEmpresa, @PathVariable("idProducto") String idProducto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Producto producto = productosService.GetSingleProduct(user.getUsuarioEmpresa(), idEmpresa, idProducto);
      ProductoVisibilidad productoVisibilidad = new ProductoVisibilidad(producto.getEmpresasConVisibilidad(), producto.getGruposConVisibilidad());

      return ok(new Representacion<ProductoVisibilidad>(HttpStatus.OK.value(), productoVisibilidad));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/{a:empresas|empresa}/{idEmpresa}/{idProducto}/pallet")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerInformacionPallet(@PathVariable("idEmpresa") String businessId, @PathVariable("idProducto") String productId) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Producto producto = productosService.GetSingleProduct(user.getUsuarioEmpresa(), businessId, productId);
      PalletResponse pallet = Cloner.MAPPER.clone(producto.getPallet());

      return ok(new Representacion<PalletResponse>(HttpStatus.OK.value(), pallet));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/pallet\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/pallet\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/pallet\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/pallet\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/{a:empresas|empresa}/{idEmpresa}/{idProducto}/empaques")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerInformacionEmpaques(@PathVariable("idEmpresa") String idEmpresa, @PathVariable("idProducto") String idProducto) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Producto producto = productosService.GetSingleProduct(user.getUsuarioEmpresa(), idEmpresa, idProducto);

      return ok(new Representacion<Set<Empaque>>(HttpStatus.OK.value(), producto.getEmpaques()));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/empaques\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/empaques\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/empaques\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/{a:empresas|empresa}/{idEmpresa}/{idProducto}/empaques\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/empresas")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getAllVisible() {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      Set<Producto> productos = productosService.GetAllVisible(user.getUsuarioEmpresa());
      Set<ProductoAccionesResponse> productoAcciones = Cloner.MAPPER.clone(productos);
      return ok(new Representacion<Set<ProductoAccionesResponse>>(HttpStatus.OK.value(), productoAcciones));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/proveedores")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getEmpresasProveedoras(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "10000") Long limit, @RequestParam(defaultValue = "0") String empresa) {
    PaginadoResponse<List<Company>> response = new PaginadoResponse<List<Company>>(1L, 0L, 0L);
    UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
    PaginadoRequest pr = new PaginadoRequest(page, limit);
    try {
      Company empresaCliente = null;
      if (empresa.equals("0")) {
        empresaCliente = user.getUsuarioEmpresa().getEmpresa();
      } else {
        List<Company> empresasAsociadasUsuario = businessService.GetUserBusinesses(user);
        for (Company emp : empresasAsociadasUsuario) {
          if (emp.getId().equals(empresa)) {
            empresaCliente = empresasDAO.findById(emp.getId());
            break;
          }
        }
      }
      if (empresaCliente == null) {
        throw new ServiceException("La Empresa no existe o el Usuario activo no está relacionado con ella");
      }
      response = this.businessService.getProviders(pr, user, empresaCliente);
      response.inicializarLink("/productos/proveedores");
      return ok(new Representacion<List<Company>>(HttpStatus.OK.value(), response));
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/proveedores\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/proveedores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/proveedores\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/proveedores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  /*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
  @GetMapping("/catalogo")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public Representacion<List<Company>> getEmpresasCatalogo(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "10000") Long limit, @RequestParam(defaultValue = "0") Long empresa) {
    PaginadoResponse<List<Company>> response = new PaginadoResponse<List<Company>>(1L, 0L, 0L);
    UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
    PaginadoRequest pr = new PaginadoRequest(page, limit);
    try {
      Company empresaCliente = null;
      if (empresa == 0) {
        empresaCliente = user.getUsuarioEmpresa().getEmpresa();
      } else {
        List<Company> empresasAsociadasUsuario = businessService.GetUserBusinesses(user);
        for (Company emp : empresasAsociadasUsuario) {
          if (emp.getId().equals(empresa)) {
            empresaCliente = empresasDAO.findById(emp.getId());
            break;
          }
        }
      }
      if (empresaCliente == null) {
        throw new ServiceException("La Empresa no existe o el Usuario activo no está relacionado con ella");
      }
      response = this.businessService.getEmpresasConProductosVisibles(pr, user, empresaCliente);
      response.inicializarLink("/productos/catalogo");
      return new Representacion<List<Company>>(HttpStatus.OK.value(), response);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/catalogo\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/catalogo\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/catalogo\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/catalogo\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

}