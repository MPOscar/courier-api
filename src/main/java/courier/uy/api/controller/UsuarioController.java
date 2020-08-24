package courier.uy.api.controller;

import static org.springframework.http.ResponseEntity.ok;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.resources.dto.UsuarioBasic;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.IUserService;
import courier.uy.core.resources.dto.LoginResponse;
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
@RequestMapping("/usuarios")
public class UsuarioController {
	Logger logger = LogManager.getLogger(UsuarioController.class);
	private final IUserService userService;
	private final IErrorService errorService;

	private final IAuthenticationFacade authenticationFacade;

	public UsuarioController(IUserService userService, IErrorService errorService, IAuthenticationFacade authenticationFacade) {
		this.authenticationFacade = authenticationFacade;
		this.errorService = errorService;
		this.userService = userService;
	}

	// ================================================================================
	// INICIO CRUD Usuario
	// ================================================================================

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("{id}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity obtenerDatosUsuarioEmpresa(@PathVariable("id") String idUsuario) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			Optional<Usuario> usuario = this.userService.obtenerDatosUsuarioEmpresa(idUsuario,
					existingUser.getUsuarioEmpresa());
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), usuario.get()));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (NoSuchElementException ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), (Usuario) null));
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed({ Roles.BUSINESS_ADMIN, Roles.SYSTEM_ADMIN })*/
	@GetMapping("{id}/empresas")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity obtenerEmpresasAsociadasUsuario(@PathVariable("id") String idUsuario) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			Optional<Usuario> usuario = this.userService.obtenerDatosUsuarioEmpresa(idUsuario,
					existingUser.getUsuarioEmpresa());
			Set<Company> listaEmpresas = new HashSet<Company>(usuario.get().getEmpresas());
			return ok(new Representacion<Set<Company>>(HttpStatus.OK.value(), listaEmpresas));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"{id}/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"{id}/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (NoSuchElementException ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"{id}/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"{id}/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			return ok(new Representacion<Set<Company>>(HttpStatus.OK.value(), new HashSet<Company>()));
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"{id}/empresas\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"{id}/empresas\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurrió un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PutMapping("")
	public ResponseEntity modificarUsuario(@RequestBody Usuario usuario) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.Modify(existingUser, usuario);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), usuario));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@GetMapping("/administradores")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity getAdministrators() {
		try {
			List<Usuario> systemAdmins = this.userService.GetSystemAdmins();
			return ok(new Representacion<List<Usuario>>(HttpStatus.OK.value(), systemAdmins));
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"/administradores\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"/administradores\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@GetMapping("")
	public ResponseEntity getUsers() {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			List<Usuario> usersFromBusiness = this.userService
					.GetAllFromBusiness(existingUser.getUsuarioEmpresa().getEmpresa());
			return ok(new Representacion<List<Usuario>>(HttpStatus.OK.value(), usersFromBusiness));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @GetMapping(\"\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @GetMapping(\"\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@DeleteMapping("/administradores/{id}")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity eliminarAdministrador(@PathVariable("id") String id) {
		try {
			this.userService.DeleteAdmin(id);
			return ok(new Representacion<String>(HttpStatus.OK.value(), "Eliminado con éxito"));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @DeleteMapping(\"/administradores/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/administradores/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @DeleteMapping(\"/administradores/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/administradores/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	// ================================================================================
	// FIN CRUD Usuario
	// ================================================================================

	// ================================================================================
	// INICIO Flujo Login Seguridad
	// ================================================================================

	@PostMapping("/registro")
	public ResponseEntity registro(@RequestBody Usuario usuario) {
		try {
			Usuario newUser = this.userService.Register(usuario);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), newUser));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/registro\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/registro\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/registro\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/registro\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}


	@PutMapping("/completarInvitacion")
	public ResponseEntity completarInvitacion(@RequestBody Usuario usuario) {
		try {
			Usuario result = this.userService.FinishRegister(usuario);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), result));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"/completarInvitacion\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"/completarInvitacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"/completarInvitacion\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"/completarInvitacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PostMapping("/enviarEmail")
	public ResponseEntity enviarEmail(@RequestBody Usuario usuario) {
		try {
			this.userService.enviarEmailConfirmacion(usuario);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), usuario));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/enviarEmail\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/enviarEmail\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/enviarEmail\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/enviarEmail\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PutMapping("confirmar/{code}")
	public ResponseEntity confirmarRegistro(@PathVariable("code") String code) {
		try {
			LoginResponse response = this.userService.Confirm(code);
			return ok(new Representacion<LoginResponse>(HttpStatus.OK.value(), response));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"confirmar/{code}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"confirmar/{code}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"confirmar/{code}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"confirmar/{code}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PutMapping("aceptarInvitacion/{code}")
	public ResponseEntity aceptarInvitacion(@PathVariable("code") String code) {
		try {
			LoginResponse response = this.userService.AcceptInvitation(code);
			return ok(new Representacion<LoginResponse>(HttpStatus.OK.value(), response));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"aceptarInvitacion/{code}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"aceptarInvitacion/{code}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"aceptarInvitacion/{code}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"aceptarInvitacion/{code}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@Path("/desvincular/{id}")*/
	@DeleteMapping("/desvincular/{id}")
	public ResponseEntity desvincularUsuario(@PathVariable("id") String id) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.RemoveFromBusiness(existingUser, id);
			return ok(new Representacion<String>(HttpStatus.OK.value(), "Desvinculado con éxito"));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @DeleteMapping(\"/desvincular/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/desvincular/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @DeleteMapping(\"/desvincular/{id}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @DeleteMapping(\"/desvincular/{id}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.BUSINESS_ADMIN)*/
	@PostMapping("/invitar")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity altaProveedor(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			Usuario user = this.userService.InviteToBusiness(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), user));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/invitar\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/invitar\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/invitar\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/invitar\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PostMapping("/invitarAdministrador")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity altaAdministrador(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			Usuario user = this.userService.InviteAdmin(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), user));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/invitarAdministrador\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/invitarAdministrador\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/invitarAdministrador\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/invitarAdministrador\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.BUSINESS_ADMIN)*/
	@PostMapping("/enviarInvitacion")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity enviarInvitacion(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.ReSendInvitation(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), newUser));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/enviarInvitacion\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/enviarInvitacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/enviarInvitacion\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/enviarInvitacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PostMapping("/enviarInvitacionAdministrador")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity enviarInvitacionAdministradr(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.ReSendInvitationAdmin(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), newUser));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/enviarInvitacionAdministrador\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/enviarInvitacionAdministrador\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/enviarInvitacionAdministrador\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/enviarInvitacionAdministrador\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.BUSINESS_ADMIN)*/
	@PostMapping("/cancelarInvitacion")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity cancelarInvitacion(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.CancelInvitation(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), newUser));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/cancelarInvitacion\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/cancelarInvitacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/cancelarInvitacion\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/cancelarInvitacion\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.SYSTEM_ADMIN)*/
	@PostMapping("/cancelarInvitacionAdministrador")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity cancelarInvitacionAdministrador(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.CancelInvitationAdmin(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), newUser));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/cancelarInvitacionAdministrador\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/cancelarInvitacionAdministrador\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/cancelarInvitacionAdministrador\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/cancelarInvitacionAdministrador\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.BUSINESS_ADMIN)*/
	@PutMapping("/cambiarPermisos")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity editarProveedor(@RequestBody Usuario newUser) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.ModifyEmployee(existingUser, newUser);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), newUser));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"/cambiarPermisos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"/cambiarPermisos\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"/cambiarPermisos\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"/cambiarPermisos\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	/*@RolesAllowed(Roles.BUSINESS_ADMIN)*/
	@PostMapping("/solicitudReseteoContrasena")
	@PreAuthorize("hasRole(T(courier.uy.core.enums.Role).BUSINESS_ADMIN) OR hasRole(T(courier.uy.core.enums.Role).SYSTEM_ADMIN)")
	public ResponseEntity enviarReseteoContrasena(@RequestBody Usuario toReset) {
		try {
			UsuarioPrincipal existingUser = authenticationFacade.getPrincipalAuth();
			this.userService.SendPasswordReset(existingUser, toReset);
			return ok(new Representacion<Usuario>(HttpStatus.OK.value(), toReset));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/solicitudReseteoContrasena\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/solicitudReseteoContrasena\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/solicitudReseteoContrasena\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/solicitudReseteoContrasena\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

	@PostMapping("/solicitudReseteoContrasenaPersonal")
	public ResponseEntity enviarReseteoContrasenaPersonal(@RequestBody UsuarioBasic usuario) {
		try {
			this.userService.SendPasswordReset(usuario);
			return ok(new Representacion<UsuarioBasic>(HttpStatus.OK.value(), usuario));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/solicitudReseteoContrasenaPersonal\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/solicitudReseteoContrasenaPersonal\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			return null;
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PostMapping(\"/solicitudReseteoContrasenaPersonal\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PostMapping(\"/solicitudReseteoContrasenaPersonal\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			return null;
		}
	}

	@PutMapping("cambiarContrasena/{code}")
	public ResponseEntity cambiarContrasena(@RequestBody UsuarioBasic usuario, @PathVariable("code") String code) {
		try {
			LoginResponse response = this.userService.ChangePassword(code, usuario.getContrasena());
			return ok(new Representacion<LoginResponse>(HttpStatus.OK.value(), response));
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"cambiarContrasena/{code}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"cambiarContrasena/{code}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "usuario controller @PutMapping(\"cambiarContrasena/{code}\") Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("usuario controller @PutMapping(\"cambiarContrasena/{code}\") Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}

}