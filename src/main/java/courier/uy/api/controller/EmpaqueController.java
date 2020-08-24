package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;

import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IEmpaquesService;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empaques")
public class EmpaqueController {

	Logger logger = LogManager.getLogger(EmpaqueController.class);

	private final IAuthenticationFacade authenticationFacade;
	private final IEmpaquesService empaquesService;
	private final IErrorService errorService;

	public EmpaqueController(IAuthenticationFacade authenticationFacade, IEmpaquesService empaquesService, IErrorService errorService) {
		this.authenticationFacade = authenticationFacade;
		this.empaquesService = empaquesService;
		this.errorService = errorService;
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getPresentaciones(@RequestParam(defaultValue = "1") Long page, @RequestParam(defaultValue = "1000") Long limit) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			UsuarioEmpresa ue = usuario.getUsuarioEmpresa();
			PaginadoRequest pr = new PaginadoRequest(page, limit);
			PaginadoResponse<List<Empaque>> empaquesResponse = this.empaquesService.obtenerEmpaques(pr, ue);
			empaquesResponse.inicializarLink("/empaques");
			Representacion<List<Empaque>> response = new Representacion<List<Empaque>>(HttpStatus.OK.value(),
					empaquesResponse);
			return ok(response);
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empaque controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empaque controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empaque controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empaque controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/{id}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getEmpaque(@PathVariable("id") String idEmpaque) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			UsuarioEmpresa ue = usuario.getUsuarioEmpresa();
			Optional<Empaque> empaque = this.empaquesService.obtenerEmpaque(idEmpaque, ue);
			if (empaque.isPresent())
				return ok(new Representacion<Empaque>(HttpStatus.OK.value(), empaque.get()));
			return ok(new Representacion<Empaque>(HttpStatus.OK.value(), (Empaque) null));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "empaque controller @GetMapping(\"/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empaque controller @GetMapping(\"/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "empaque controller @GetMapping(\"/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("empaque controller @GetMapping(\"/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("/{id}/empresa") //TODO revisar que hace el viejo dropwizard
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getEmpresaEmpaque(@PathVariable("id") String idEmpaque) {
		UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
		ResponseEntity representacionEmpaque = this.getEmpaque(idEmpaque);
		/*if (representacionEmpaque.getData() == null)
			throw new WebApplicationException("No se encontró el Empque con id " + idEmpaque,
					HttpStatus.BAD_REQUEST_400);*/
		return ok(representacionEmpaque);
	}

}