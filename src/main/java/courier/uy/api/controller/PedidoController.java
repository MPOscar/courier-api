package courier.uy.api.controller;

import courier.uy.core.db.*;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IBusinessService;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IPedidoService;
import courier.uy.core.utils.IEmailHelper;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/pedido")
public class PedidoController {
  Logger logger = LogManager.getLogger(PedidoController.class);
  private final IPedidoService pedidoService;
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

  public PedidoController(IPedidoService pedidoService, IBusinessService businessService,
                          IErrorService errorService, ProductosDAO productosDAO, CategoriasDAO categoriasDAO,
                          PresentacionesDAO presentacionesDAO, EmpaquesDAO empaquesDAO, ParamsDAO paramsDAO, EmpresasDAO empresasDAO,
                          IEmailHelper emailHelper, IAuthenticationFacade authenticationFacade) {
    this.pedidoService = pedidoService;
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
  @GetMapping("/listaDeVenta/{listaDeVenta}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerProductosVisiblesListaVentaEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "100") Long limit, @RequestParam(defaultValue = "") String division,
                                                                  @RequestParam(defaultValue = "marca") String orderBy, @PathVariable("listaDeVenta") String listaDeVenta,  @RequestParam(defaultValue = "false") Boolean suspendidos, @RequestParam(defaultValue = "false") Boolean discontinuados, @RequestParam(defaultValue = "false") Boolean ocultarSuspendidos, @RequestParam(defaultValue = "false") Boolean ocultarDiscontinuados,
                                                                  @RequestParam(defaultValue = "") List<String> filters, @RequestParam(defaultValue = "") List<String> marcas,
                                                                  @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones,
                                                                  @RequestParam(defaultValue = "description") String sort) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setOrderBy(orderBy);
      paginadoRequest.setDivision(division);
      paginadoRequest.setDiscontinuados(discontinuados);
      paginadoRequest.setSuspendidos(suspendidos);
      paginadoRequest.setOcultarDiscontinuados(ocultarDiscontinuados);
      paginadoRequest.setOcultarSuspendidos(ocultarSuspendidos);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      Company empresa = user.getUsuarioEmpresa().getEmpresa();
      List<Producto> productosResponse = this.pedidoService.GetVisibleByBussinesOnSaleList(paginadoRequest, listaDeVenta, user.getUsuarioEmpresa());
      return ok(productosResponse);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("pedido controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  //@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })
  @GetMapping("/divisiones/{listaDeVenta}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerDivisionesProductosVisiblesListaVentaEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("listaDeVenta") String listaDeVenta, @RequestParam(defaultValue = "false") Boolean suspendidos, @RequestParam(defaultValue = "false") Boolean discontinuados, @RequestParam(defaultValue = "false") Boolean ocultarSuspendidos, @RequestParam(defaultValue = "false") Boolean ocultarDiscontinuados,
                                                                            @RequestParam(defaultValue = "") List<String> filters, @RequestParam(defaultValue = "") List<String> marcas,
                                                                            @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones,
                                                                            @RequestParam(defaultValue = "description") String sort) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setDiscontinuados(discontinuados);
      paginadoRequest.setSuspendidos(suspendidos);
      paginadoRequest.setOcultarDiscontinuados(ocultarDiscontinuados);
      paginadoRequest.setOcultarSuspendidos(ocultarSuspendidos);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      HashMap<String, Integer> pedidoResponse = this.pedidoService.GetDivisionesVisibleByBussinesOnSaleList(listaDeVenta, user.getUsuarioEmpresa(), paginadoRequest);
      return ok(pedidoResponse);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("pedido controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  //@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })
  @GetMapping("/marcas/{listaDeVenta}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerMarcasProductosVisiblesListaVentaEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("listaDeVenta") String listaDeVenta, @RequestParam(defaultValue = "false") Boolean suspendidos, @RequestParam(defaultValue = "false") Boolean discontinuados, @RequestParam(defaultValue = "false") Boolean ocultarSuspendidos, @RequestParam(defaultValue = "false") Boolean ocultarDiscontinuados,
                                                                            @RequestParam(defaultValue = "") List<String> filters, @RequestParam(defaultValue = "") List<String> marcas,
                                                                            @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones,
                                                                            @RequestParam(defaultValue = "description") String sort) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setDiscontinuados(discontinuados);
      paginadoRequest.setSuspendidos(suspendidos);
      paginadoRequest.setOcultarDiscontinuados(ocultarDiscontinuados);
      paginadoRequest.setOcultarSuspendidos(ocultarSuspendidos);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      HashMap<String, Integer> pedidoResponse = this.pedidoService.GetMarcasVisibleByBussinesOnSaleList(listaDeVenta, user.getUsuarioEmpresa(), paginadoRequest);
      return ok(pedidoResponse);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("pedido controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  //@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })
  @GetMapping("/lineas/{listaDeVenta}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity obtenerLineasProductosVisiblesListaVentaEmpresa(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit, @PathVariable("listaDeVenta") String listaDeVenta, @RequestParam(defaultValue = "false") Boolean suspendidos, @RequestParam(defaultValue = "false") Boolean discontinuados, @RequestParam(defaultValue = "false") Boolean ocultarSuspendidos, @RequestParam(defaultValue = "false") Boolean ocultarDiscontinuados,
                                                                        @RequestParam(defaultValue = "") List<String> filters, @RequestParam(defaultValue = "") List<String> marcas,
                                                                        @RequestParam(defaultValue = "") List<String> lineas, @RequestParam(defaultValue = "") List<String> divisiones,
                                                                        @RequestParam(defaultValue = "description") String sort) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      PaginadoRequest paginadoRequest = new PaginadoRequest(page, limit);
      paginadoRequest.setDiscontinuados(discontinuados);
      paginadoRequest.setSuspendidos(suspendidos);
      paginadoRequest.setOcultarDiscontinuados(ocultarDiscontinuados);
      paginadoRequest.setOcultarSuspendidos(ocultarSuspendidos);
      paginadoRequest.setFilters(filters);
      paginadoRequest.setMarcas(marcas);
      paginadoRequest.setDivisiones(divisiones);
      paginadoRequest.setLineas(lineas);
      paginadoRequest.setSort(sort);
      HashMap<String, Integer> pedidoResponse = this.pedidoService.GetLineasVisibleByBussinesOnSaleList(listaDeVenta, user.getUsuarioEmpresa(), paginadoRequest);
      return ok(pedidoResponse);
    } catch (ServiceException ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    } catch (Exception ex) {
      logger.log(Level.ERROR, "pedido controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("pedido controller @GetMapping(\"\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

}