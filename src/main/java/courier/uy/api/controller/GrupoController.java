package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

import courier.uy.core.entity.Company;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IGroupService;
import courier.uy.core.entity.Grupo;
import courier.uy.core.resources.dto.UsuarioPrincipal;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grupos")
public class GrupoController {
	Logger logger = LogManager.getLogger(GrupoController.class);
	private final IAuthenticationFacade authenticationFacade;
	private final IGroupService gruposService;
	private final IErrorService errorService;

	public GrupoController(IAuthenticationFacade authenticationFacade, IGroupService gService, IErrorService eService) {
		this.authenticationFacade = authenticationFacade;
		this.gruposService = gService;
		this.errorService = eService;
	}

	/*@RolesAllowed({ Roles.BUSINESS_USER, Roles.BUSINESS_ADMIN })*/
	@Transactional
	@GetMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_USER) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity get() {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Set<Grupo> allGroups = this.gruposService.GetAllFromProvider(usuario);
			List<Grupo> toReturn = new ArrayList<Grupo>();
			for (Grupo g : allGroups) {
				if (!g.getEliminado()) {
					Set<Company> businesses = g.getEmpresas();
					g.setEmpresas(businesses);
					toReturn.add(g);
				}
			}

			return ok(new Representacion<List<Grupo>>(HttpStatus.OK.value(), toReturn));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "grupo controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "grupo controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_GROUPS })*/
	@PostMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_GROUPS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity crearGrupo(@RequestBody Grupo grupo) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Grupo creado = this.gruposService.Create(grupo, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Grupo>(HttpStatus.OK.value(), creado));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "grupo controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @PostMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "grupo controller @PostMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @PostMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_GROUPS })*/
	@PutMapping("")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_GROUPS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity modificarGrupo(@RequestBody Grupo grupo) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Grupo modificado = this.gruposService.Modify(grupo, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Grupo>(HttpStatus.OK.value(), modificado));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "grupo controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "grupo controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.EDIT_GROUPS })*/
	@DeleteMapping("/{groupId}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).EDIT_GROUPS) OR hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity eliminarGrupo(@PathVariable("groupId") String groupId) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			Grupo grupo = this.gruposService.GetGroupById(groupId);
			this.gruposService.Delete(grupo, usuario.getUsuarioEmpresa());
			return ok(new Representacion<Grupo>(HttpStatus.OK.value(), grupo));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "grupo controller @DeleteMapping(\"/{groupId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("grupo controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "grupo controller @DeleteMapping(\"/{groupId}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService.Log("Server Error en empresas/grupos: " + ex.getMessage());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
}
