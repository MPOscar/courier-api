package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IFileService;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
	Logger logger = LogManager.getLogger(FileController.class);
	private final IFileService fileService;
	private final IErrorService errorService;

	private final IAuthenticationFacade authenticationFacade;

	public FileController(IFileService fileService, IErrorService errorService, IAuthenticationFacade authenticationFacade) {
		this.fileService = fileService;
		this.errorService = errorService;
		this.authenticationFacade = authenticationFacade;
	}

	@PostMapping("/producto/{productId}")
	public ResponseEntity saveFileForProduct(@RequestParam("file") MultipartFile file, @PathVariable("productId") String productId) {
		try {
			String location = this.fileService.SaveFileForProduct(file, productId);
			return ok(new Representacion<String>(HttpStatus.OK.value(), location));

		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "file controller @PostMapping(\"/producto/{productId}\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());

		} catch (Exception ex) {
			logger.log(Level.ERROR, "file controller @PostMapping(\"/producto/{productId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en productos/excel: " + ex.getMessage());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PostMapping("/empresa")
	public ResponseEntity saveFileForBussisnes(@RequestParam("file") MultipartFile file) {
		try {
			UsuarioPrincipal user = authenticationFacade.getPrincipalAuth();
			String location = this.fileService.SaveFileForBussisnes(file, user.getUsuarioEmpresa());
			return ok(new Representacion<String>(HttpStatus.OK.value(), location));

		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "file controller @PostMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());

		} catch (Exception ex) {
			logger.log(Level.ERROR, "file controller @PostMapping(\"/empresa\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en productos/excel: " + ex.getMessage());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PostMapping("/empresa/{rut}")
	public ResponseEntity saveMultiplesFilesForBussisnes(@RequestParam("files") MultipartFile[] files, @PathVariable("rut") Long rut) {
		try {
			List<String> result = this.fileService.SaveFileByRutBussisnes(rut, files);
			return ok(new Representacion<List<String>>(HttpStatus.OK.value(), result));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "file controller @PostMapping(\"/empresa/{rut}\") Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());

		} catch (Exception ex) {
			logger.log(Level.ERROR, "file controller @PostMapping(\"/empresa/{rut}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en productos/excel: " + ex.getMessage());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

}
