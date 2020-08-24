package courier.uy.api.controller;

import courier.uy.core.db.*;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IPrecioService;
import courier.uy.core.entity.Precio;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.services.interfaces.IProductService;
import courier.uy.core.services.interfaces.ISaleListService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/precio")
public class PrecioController {
    Logger logger = LogManager.getLogger(PrecioController.class);
    @Autowired
    private final ISaleListService saleListService;
    @SuppressWarnings("unused")
    @Autowired
    private final IProductService productService;
    @Autowired
    private final IPrecioService precioService;
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

    public PrecioController(ISaleListService saleListService, IProductService productService,
                            IPrecioService precioService, UbicacionesDAO ubucacionesDAO, EmpresasDAO empresasDAO, ListasDeVentaDAO listaDeVentaDAO,
                            ProductosDAO productosDAO, ProductoYListaDeVentaDAO productoYListaDeVentaDAO,
                            EmpresaYListaDeVentaDAO empresaYListaDeVentaDAO, GrupoYListaDeVentaDAO grupoYListaDeVentaDAO,
                            IErrorService eService, IAuthenticationFacade authenticationFacade) {
        this.precioService = precioService;
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


    /*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_GROUPS })*/
    @GetMapping("/{productoCpp}/{listaVentaGln}")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity obtenerPrecioProductoPorCppGln(@PathVariable("productoCpp") String productoCpp, @PathVariable("listaVentaGln") String listaVentaGln) {
        try {
            UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
            List<Precio> precios = this.precioService.GetPrecioByCppAndGln(productoCpp, listaVentaGln);
            return ok(new Representacion<List<Precio>>(HttpStatus.OK.value(), precios));
        } catch (Exception ex) {
            logger.log(Level.ERROR, "grupo controller @DeleteMapping(\"/{groupId}\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService.Log("Server Error en empresas/grupos: " + ex.getMessage());
            throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /* @RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_SALELISTS }) */
    @PostMapping("/excel/actualizar")
    @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
    public ResponseEntity sincronizarPorExcelPrecios(@RequestParam("precios") MultipartFile precios) {
        try {
            UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
            String returnString;
            InputStream inputStreamProductos = precios.getInputStream();
            precioService.actualizarPrecios(inputStreamProductos);
            return ok("Se actualizaron correctamente los precios");
        } catch (Exception ex) {
            logger.log(Level.ERROR, "precios controller @PostMapping(\"/excel/actualizar\") Error:", ex.getMessage(), ex.getStackTrace());
            this.errorService
                    .Log("precios controller @PostMapping(\"/excel/actualizar\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
            throw new WebApplicationException("Ocurrió un error al actualizar los precios - " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }
}
