package courier.uy.api.controller;

import courier.uy.core.db.ProductosDAO;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.ILaboratorioService;
import courier.uy.api.dto.ProductoLaboratorioDTO;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.services.interfaces.IProductService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.WebApplicationException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/productosLaboratorio")
public class CincoDeOroController {

  Logger logger = LogManager.getLogger(CincoDeOroController.class);

  @Autowired
  private IProductService productosService;
  @Autowired
  private ProductosDAO productosDAO;
  @Autowired
  private IErrorService errorService;
  @Autowired
  private ILaboratorioService laboratorioService;

  @Value("${bucket:rondanet}")
  private final String bucket = "rondanet";

  private final IAuthenticationFacade authenticationFacade;

  public CincoDeOroController(IAuthenticationFacade authenticationFacade) {
    this.authenticationFacade = authenticationFacade;
  }

  @GetMapping("/{gtin}")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getProductoAtributos(@PathVariable("gtin") String gtin) {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      ProductoLaboratorioDTO productoLaboratorioDTO = laboratorioService.getProductoLaboratorio(user.getUsuarioEmpresa(), gtin);
      return ok(new Representacion<ProductoLaboratorioDTO>(HttpStatus.OK.value(), productoLaboratorioDTO));
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  @GetMapping("")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity getAllProductosLaboratorio() {
    try {
      UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
      List<ProductoLaboratorioDTO> productosLaboratorioDTO = laboratorioService.getAllProductoLaboratorio(user.getUsuarioEmpresa());
      return ok(new Representacion<List<ProductoLaboratorioDTO>>(HttpStatus.OK.value(), productosLaboratorioDTO));
    } catch (Exception ex) {
      logger.log(Level.ERROR, "producto controller @GetMapping(\"/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("producto controller @GetMapping(\"/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

  @PostMapping("/actualizar")
  @PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
  public ResponseEntity sincronizarPorExcelPrecios(@RequestParam("productos") MultipartFile productos) {
    try {
      UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
      InputStream inputStreamProductos = productos.getInputStream();
      HashMap<String, String> result = laboratorioService.actualizarProductos(inputStreamProductos, usuario.getUsuarioEmpresa());
      return ok(" Productos creados " + result.get("productosInsertados") +
              "\n Productos actualizados " + result.get("productosActualizados") +
              "\n Kit de Productos con error " + result.get("kitDeProductosConError") +
              "\n Productos con error " + result.get("productosConError") +
              "\n Total de Errores " + result.get("totalDeErrores") +
              "\n\n Errores encontrados en Kit de Productos: \n" + result.get("erroresKit")  +
              "\n Errores encontrados en Productos: \n" + result.get("erroresProductos"));
    } catch (Exception ex) {
      logger.log(Level.ERROR, "precios controller @PostMapping(\"/excel/actualizar\") Error:", ex.getMessage(), ex.getStackTrace());
      this.errorService
              .Log("AtributosLaboratorioController controller @PostMapping(\"/excel/actualizar\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
      throw new WebApplicationException("Ocurrió un error al actualizar los productos - " + ex.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

  }

}