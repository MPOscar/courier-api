package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

import courier.uy.api.dto.ListaDeVentaDTO;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.db.ProductosDAO;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.resources.dto.*;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.services.interfaces.IProductService;
import courier.uy.core.services.interfaces.ISaleListService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import courier.uy.core.db.EmpresaYListaDeVentaDAO;
import courier.uy.core.db.EmpresasDAO;
import courier.uy.core.db.GrupoYListaDeVentaDAO;
import courier.uy.core.db.ListasDeVentaDAO;
import courier.uy.core.db.ProductoYListaDeVentaDAO;
import courier.uy.core.db.UbicacionesDAO;

@RestController
@RequestMapping("/listasDeVenta")
public class ListaDeVentaController {
    Logger logger = LogManager.getLogger(ListaDeVentaController.class);
    @Autowired
    private final ISaleListService saleListService;
    @SuppressWarnings("unused")
    @Autowired
    private final IProductService productService;
    @Autowired
    private final UbicacionesDAO ubucacionesDAO;
    @Autowired
    private final EmpresasDAO empresasDAO;
    @Autowired
    private final ListasDeVentaDAO listaDeVentaDAO;
    @Autowired
    private final IErrorService errorService;
    @Autowired
    private final ProductosDAO productosDAO;
    @Autowired
    private final ProductoYListaDeVentaDAO productoYListaDeVentaDAO;
    @Autowired
    private final GrupoYListaDeVentaDAO grupoYListaDeVentaDAO;
    @Autowired
    private final EmpresaYListaDeVentaDAO empresaYListaDeVentaDAO;

    private final IAuthenticationFacade authenticationFacade;

    public ListaDeVentaController(ISaleListService saleListService, IProductService productService,
            UbicacionesDAO ubucacionesDAO, EmpresasDAO empresasDAO, ListasDeVentaDAO listaDeVentaDAO,
            ProductosDAO productosDAO, ProductoYListaDeVentaDAO productoYListaDeVentaDAO,
            EmpresaYListaDeVentaDAO empresaYListaDeVentaDAO, GrupoYListaDeVentaDAO grupoYListaDeVentaDAO,
            IErrorService eService, IAuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
        this.saleListService = saleListService;
        this.productService = productService;
        this.ubucacionesDAO = ubucacionesDAO;
        this.listaDeVentaDAO = listaDeVentaDAO;
        this.empresasDAO = empresasDAO;
        this.productosDAO = productosDAO;
        this.productoYListaDeVentaDAO = productoYListaDeVentaDAO;
        this.empresaYListaDeVentaDAO = empresaYListaDeVentaDAO;
        this.grupoYListaDeVentaDAO = grupoYListaDeVentaDAO;
        this.errorService = eService;
    }


    @GetMapping("/empresa")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity getSaleList(@RequestParam(defaultValue = "1") Long page,
                                                             @RequestParam(defaultValue = "1000") Long limit) {
        try {
            UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
            PaginadoRequest pr = new PaginadoRequest(page, limit);
            PaginadoResponse<List<ListaVentaBasic>> listaVentaResponse = this.saleListService
                    .GetAllSaleListFromProvider(user);
            listaVentaResponse.inicializarLink("/listasDeVenta");
            Representacion<List<ListaVentaBasic>> response = new Representacion<List<ListaVentaBasic>>(
                    HttpStatus.OK.value(), listaVentaResponse);
            return ok(response);
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller get: /supermarketPedido/{id} Error: ", ex.getMessage(), ex.getStackTrace());
            this.errorService.Log("lista de venta controller get: /supermarketPedido/{id} Error: " + ex.getMessage(),
                    " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller get: /supermarketPedido/{id} Error: ", ex.getMessage(), ex.getStackTrace());
            this.errorService.Log("lista de venta controller get: /supermarketPedido/{id} Error: " + ex.getMessage(),
                    " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN }) */
    @Transactional
    @GetMapping("")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity get() {
        try {
            UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
            Set<ListaDeVenta> allSaleLists = this.saleListService.GetAllFromProvider(user);
            List<ListaDeVenta> toReturn = new ArrayList<ListaDeVenta>();
            return ok(new Representacion<List<ListaDeVenta>>(HttpStatus.OK.value(), toReturn));
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_SALELISTS }) */
    @Transactional
    @GetMapping("/{slId}")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_SALELISTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity obtenerListaDeVenta(@PathVariable("slId") String slId) {
        try {
            UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
            Company emp = usuario.getUsuarioEmpresa().getEmpresa();
            ListaDeVenta lv = this.saleListService.GetById(slId, emp.getId());
            ListaDeVentaDTO listaDeVentaDTO = new ListaDeVentaDTO(lv);
            return ok(new Representacion<ListaDeVentaDTO>(HttpStatus.OK.value(), listaDeVentaDTO));
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"/{slId}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"/{slId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"/{slId}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"/{slId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN }) */
    @GetMapping("/supermarket/{id}")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity getForSupermarket(@RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "1000") Long limit, @PathVariable("id") String pId) {
        try {
            UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
            PaginadoRequest pr = new PaginadoRequest(page, limit);
            PaginadoResponse<List<ListaDeVenta>> listaVentaResponse = this.saleListService
                    .GetAllFromProviderForSupermarket(pr, user, pId);
            listaVentaResponse.inicializarLink("/listasDeVenta");
            Representacion<List<ListaDeVenta>> response = new Representacion<List<ListaDeVenta>>(HttpStatus.OK.value(),
                    listaVentaResponse);

            return ok(response);
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"/supermarket/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"/supermarket/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"/supermarket/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"/supermarket/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @GetMapping("/supermarketPedido/{id}")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity getForSupermarketPedido(@RequestParam(defaultValue = "1") Long page,
                                                                         @RequestParam(defaultValue = "1000") Long limit, @PathVariable("id") String pId) {
        try {
            UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
            PaginadoRequest pr = new PaginadoRequest(page, limit);
            PaginadoResponse<List<ListaVentaBasic>> listaVentaResponse = this.saleListService
                    .GetAllFromProviderForSupermarketPedido(pr, user, pId);
            listaVentaResponse.inicializarLink("/listasDeVenta");
            Representacion<List<ListaVentaBasic>> response = new Representacion<List<ListaVentaBasic>>(HttpStatus.OK.value(),
                    listaVentaResponse);
            return ok(response);
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"/supermarketPedido/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"/supermarketPedido/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());;
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @GetMapping(\"/supermarketPedido/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @GetMapping(\"/supermarketPedido/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());;
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_SALELISTS }) */
    @PostMapping("")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_SALELISTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity crearListaDeVenta(@RequestBody ListaDeVenta lv) {
        try {
            UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
            ListaDeVenta creado = this.saleListService.Create(lv, user.getUsuarioEmpresa());
            return ok(new Representacion<ListaDeVenta>(HttpStatus.OK.value(), creado));
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @PostMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @PostMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_SALELISTS }) */
    @PutMapping("")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_SALELISTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity modificarListaDeVenta(@RequestBody ListaDeVenta lv) {
        try {
            UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
            ListaDeVenta modificado = this.saleListService.Modify(lv, usuario.getUsuarioEmpresa());
            return ok(new Representacion<ListaDeVenta>(HttpStatus.OK.value(), modificado));
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_SALELISTS }) */
    @DeleteMapping("/{slId}")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_SALELISTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity eliminarListaDeVenta(@PathVariable("slId") String slId) {
        try {
            UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
            Company emp = usuario.getUsuarioEmpresa().getEmpresa();
            ListaDeVenta lv = this.saleListService.GetById(slId, emp.getId());
            this.saleListService.Delete(lv, usuario.getUsuarioEmpresa());
            return ok(new Representacion<ListaDeVenta>(HttpStatus.OK.value(), lv));
        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @DeleteMapping(\"/{slId}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @DeleteMapping(\"/{slId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @DeleteMapping(\"/{slId}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @DeleteMapping(\"/{slId}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_SALELISTS }) */
    @PostMapping("/excel/sincronizarRN/{rut}/{glnEmpresa}")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_SALELISTS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity sincronizarPorExcelRN(@RequestParam("clientes") MultipartFile clientes,
            @RequestParam("productos") MultipartFile productos, @PathVariable("rut") String rut,
            @PathVariable("glnEmpresa") String glnEmpresa, @RequestParam(defaultValue = "true") Boolean eliminarTodos) {
        /*try {
            UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
            String returnString;
            int responseCode = HttpStatus.OK.value();
            ListaDeVenta creado = new ListaDeVenta();
            InputStream isProductos = productos.getInputStream();
            InputStream isClientes = clientes.getInputStream();
            List<ExcelCliente> clientesConErrores = new ArrayList<ExcelCliente>();
            List<Empresa> listaClientes = new ArrayList<Empresa>();
            List<Producto> listaProductos = new ArrayList<Producto>();
            Ubicacion ubicacion = ubucacionesDAO.findByKey("codigo", glnEmpresa, null).get();
            List<Empresa> empresa = empresasDAO.findAllByKey("rut", rut);
            if (!empresa.isEmpty() && ubicacion != null) {

                Map<Boolean, List<ExcelRNProducto>> productosAgrupadosPorAccion = ExcelUtility
                        .procesarExcelProductosRN(isProductos).stream()
                        .collect(Collectors.partitioningBy(e -> e.getEliminar().trim().isEmpty()));

                List<ExcelProduct> productosAInsertar = this.productService
                        .crearProductosRN(productosAgrupadosPorAccion.get(true), usuario, empresa.get(0));
                List<ExcelRNCliente> listadoClientesExcel = ExcelUtility.procesarExcelClientesRN(isClientes);

                List<ExcelCliente> clientesAInsertar = this.productService.crearClientesRN(listadoClientesExcel,
                        usuario, empresa.get(0));

                int clientsLoaded = 0;
                int clientsWithErrors = 0;
                int productsLoaded = 0;
                int productsWithErrors = 0;
                for (ExcelProduct p : productosAInsertar) {
                    if (p.getWasCreated()) {
                        productsLoaded++;
                        listaProductos.add(p.getProduct());
                    }
                    if (p.getHasErrors() || !p.getWasCreated())
                        productsWithErrors++;
                }
                for (ExcelCliente p : clientesAInsertar) {
                    if (p.getWasCreated()) {
                        clientsLoaded++;
                        listaClientes.add(p.getEmpresa());
                    }
                    if (p.getHasErrors() || !p.getWasCreated()) {
                        productsWithErrors++;
                        clientesConErrores.add(p);
                    }
                }

                returnString = "Se cargaron " + productsLoaded + " producto(s) con éxito." + "\n";
                if (productsWithErrors > 1) {
                    responseCode = HttpStatus.CREATED.value();
                    returnString += " Ocurrieron " + productsWithErrors + " error(es) al cargar productos" + "\n";
                }
                returnString += "Se cargaron " + clientsLoaded + " cliente(s) con éxito." + "\n";

                if (clientsWithErrors > 1) {
                    responseCode = HttpStatus.CREATED.value();
                    returnString += " Ocurrieron " + productsWithErrors + " error(es) al cargar clientes" + "\n";
                }

                List<ListaDeVenta> lsNuevaListaDeVenta = this.listaDeVentaDAO.findByKey("descripcion",
                        ubicacion.getDescripcion());
                if (!lsNuevaListaDeVenta.isEmpty()) {
                    ListaDeVenta listaDeVenta = lsNuevaListaDeVenta.get(0);
                    listaDeVenta.setListaEmpresas(listaClientes);
                    listaDeVenta.setListaProductos(listaProductos);
                    this.listaDeVentaDAO.update(listaDeVenta);
                    if (!listaProductos.isEmpty()) {
                        productoYListaDeVentaDAO.deleteAllForSaleList(listaDeVenta.getId());
                        for (Producto producto : listaProductos) {
                            try {
                                Producto productoDB = this.productosDAO.findByCpp(producto.getCpp()).get();
                                if (productoDB != null) {
                                    ProductoYListaDeVenta productoParaListaDeVenta = new ProductoYListaDeVenta();
                                    productoParaListaDeVenta.setListaDeVentaId(listaDeVenta.getId());
                                    productoParaListaDeVenta.setProductoId(productoDB.getId());
                                    this.saleListService.listaVentaVisibilidadDeleteAll(listaDeVenta.getId(), productoDB.getId());
                                    if(this.productService.visibilidadPrivadaEnListaVenta(productosAgrupadosPorAccion.get(true), productoDB.getCpp())){
                                        productoParaListaDeVenta.setEsPrivado(true);
                                        productoParaListaDeVenta.setEsPublico(false);
                                        this.saleListService.agregarListaVentaVisibilidad(listaDeVenta.getId(), productoDB);
                                    }else{
                                        productoParaListaDeVenta.setEsPrivado(false);
                                        productoParaListaDeVenta.setEsPublico(true);
                                    }
                                    this.productoYListaDeVentaDAO.insertForSaleList(productoParaListaDeVenta);
                                }
                            } catch (Exception ex) {
                                this.errorService.Log("Server Error en ListaDeVenta/productos: " + ex.getMessage());
                            }

                        }

                    }
                    if (!listaClientes.isEmpty()) {
                        empresaYListaDeVentaDAO.deleteAllForSaleList(listaDeVenta.getId());
                        for (Empresa empresaTemp : listaClientes) {
                            try {
                                Optional<Empresa> optionalEmpresa = empresasDAO.findByKey("gln",
                                        empresaTemp.getGln().toString());
                                Empresa empresaDB = optionalEmpresa.isPresent() ? optionalEmpresa.get() : null;
                                if (empresaDB != null) {
                                    EmpresaYListaDeVenta empresaParaListaDeVenta = new EmpresaYListaDeVenta();
                                    empresaParaListaDeVenta.setListaDeVentaId(listaDeVenta.getId());
                                    empresaParaListaDeVenta.setEmpresaId(empresaDB.getId());
                                    this.empresaYListaDeVentaDAO.insertForSaleList(empresaParaListaDeVenta);
                                }
                            } catch (Exception ex) {
                                this.errorService.Log("Server Error en ListaDeVenta/empresas: " + ex.getMessage());
                            }
                        }

                        for (ExcelCliente cliente : clientesAInsertar) {
                            if (cliente.grupos.size() > 0) {
                                for (long grupoId : cliente.grupos) {
                                    GrupoYListaDeVenta grupoYListaDeVenta = new GrupoYListaDeVenta();
                                    grupoYListaDeVenta.setListaDeVentaId(listaDeVenta.getId());
                                    grupoYListaDeVenta.setGrupoId(grupoId);
                                    this.grupoYListaDeVentaDAO.insertForSaleList(grupoYListaDeVenta);
                                }
                            }
                        }
                    }
                } else {
                    ListaDeVenta nuevaListaDeVenta = new ListaDeVenta();
                    nuevaListaDeVenta.setNombre(ubicacion.getDescripcion());
                    nuevaListaDeVenta.setDescripcion(ubicacion.getDescripcion());
                    nuevaListaDeVenta.setEmpresa(empresa.get(0));
                    nuevaListaDeVenta.setListaEmpresas(listaClientes);
                    nuevaListaDeVenta.setListaProductos(listaProductos);
                    nuevaListaDeVenta.setUbicacion(ubicacion);
                    creado = this.listaDeVentaDAO.insert(nuevaListaDeVenta);
                    for (Producto producto : listaProductos) {
                        try {
                            Producto productoDB = productosDAO.findByCpp(producto.getCpp()).get();
                            if (productoDB != null) {
                                ProductoYListaDeVenta productoParaListaDeVenta = new ProductoYListaDeVenta();
                                productoParaListaDeVenta.setListaDeVentaId(creado.getId());
                                productoParaListaDeVenta.setProductoId(productoDB.getId());
                                productoParaListaDeVenta.setEsPrivado(false);
                                productoParaListaDeVenta.setEsPublico(true);
                                this.saleListService.listaVentaVisibilidadDeleteAll(creado.getId(), productoDB.getId());
                                if((productoDB.getEmpresasConVisibilidad() != null && productoDB.getEmpresasConVisibilidad().size() > 0) || (productoDB.getGruposConVisibilidad() != null && productoDB.getGruposConVisibilidad().size() > 0) ){
                                    productoParaListaDeVenta.setEsPrivado(true);
                                    productoParaListaDeVenta.setEsPublico(false);
                                    this.saleListService.agregarListaVentaVisibilidad(creado.getId(), productoDB);
                                }
                                productoYListaDeVentaDAO.insertForSaleList(productoParaListaDeVenta);
                            }
                        } catch (Exception ex) {
                            this.errorService.Log("Server Error en ListaDeVenta/productos: " + ex.getMessage());
                        }
                    }

                    for (Empresa empresaTemp : listaClientes) {
                        try {
                            Empresa empresaDB = empresasDAO.findByKey("rut", empresaTemp.getRut().toString()).get();
                            if (empresaDB != null) {
                                EmpresaYListaDeVenta empresaParaListaDeVenta = new EmpresaYListaDeVenta();
                                empresaParaListaDeVenta.setListaDeVentaId(creado.getId());
                                empresaParaListaDeVenta.setEmpresaId(empresaDB.getId());
                                this.empresaYListaDeVentaDAO.insertForSaleList(empresaParaListaDeVenta);
                            }
                        } catch (Exception ex) {
                            this.errorService.Log("Server Error en ListaDeVenta/empresas: " + ex.getMessage());
                        }
                    }

                    for (ExcelCliente cliente : clientesAInsertar) {
                        if (cliente.grupos.size() > 0) {
                            for (long grupoId : cliente.grupos) {
                                GrupoYListaDeVenta grupoYListaDeVenta = new GrupoYListaDeVenta();
                                grupoYListaDeVenta.setListaDeVentaId(creado.getId());
                                grupoYListaDeVenta.setGrupoId(grupoId);
                                this.grupoYListaDeVentaDAO.insertForSaleList(grupoYListaDeVenta);
                            }
                        }
                    }
                }
            } else {
                returnString = "La empresa con GLN: " + glnEmpresa + " y RUT " + rut + " no es valida";
                throw new ServiceException(returnString);
            }
            return ok(new Representacion<String>(responseCode, "La lista de venta fue creada correctamente."));

        } catch (ServiceException ex) {
            logger.log(Level.ERROR, "lista de venta controller @PostMapping(\"/excel/sincronizarRN/{rut}/{glnEmpresa}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @PostMapping(\"/excel/sincronizarRN/{rut}/{glnEmpresa}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        } catch (Exception ex) {
            logger.log(Level.ERROR, "lista de venta controller @PostMapping(\"/excel/sincronizarRN/{rut}/{glnEmpresa}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("lista de venta controller @PostMapping(\"/excel/sincronizarRN/{rut}/{glnEmpresa}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }*/
        return ok("");
    }
}
