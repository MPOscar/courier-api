package courier.uy.api.controller;

import javax.transaction.Transactional;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;

import courier.uy.core.resources.dto.Representacion;
import courier.uy.core.resources.dto.UsuarioBasic;
import courier.uy.core.security.IAuthenticationFacade;
import courier.uy.core.services.interfaces.IErrorService;
import courier.uy.core.services.interfaces.ILoginService;
import courier.uy.core.resources.dto.LoginResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.exceptions.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {
	@SuppressWarnings("unused")
	Logger logger = LogManager.getLogger(LoginController.class);

	private final IAuthenticationFacade authenticationFacade;
	private ILoginService loginService;
	private IErrorService errorService;

	public LoginController(ILoginService loginService, IErrorService errorService, IAuthenticationFacade authenticationFacade) {
		this.loginService = loginService;
		this.authenticationFacade = authenticationFacade;
		this.errorService = errorService;
	}

	@PostMapping("/login")
	@Transactional
	public Representacion<LoginResponse> authenticateUser(@RequestBody UsuarioBasic usuario) {
		try {
			LoginResponse response = loginService.LoginBasic(usuario);
			this.errorService.Log("login controller post: /login Exitoso: " + usuario.getUsuario());
			this.errorService
					.Log("login controller @PostMapping(\"/login\")  Error: " , " StackTrace: ");
			return new Representacion<LoginResponse>(HttpStatus.OK.value(), response);
		} catch (ServiceException ex) {
			logger.log(Level.ERROR, "login controller Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("login controller @PostMapping(\"/login\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new WebApplicationException(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
		} catch (Exception ex) {
			logger.log(Level.ERROR, "login controller Error:", ex.getMessage(), ex.getStackTrace());
			throw new WebApplicationException("Ocurró un error inesperado, intente nuevamente - " + ex.getMessage(),
					HttpStatus.UNAUTHORIZED.value());
		}
	}

	@PostMapping("/empresa/{id}")
	public Representacion<LoginResponse> empresa(@PathVariable("id") String id) {
		try {
			UsuarioPrincipal usuario = authenticationFacade.getPrincipalAuth();
			LoginResponse response = this.loginService.LoginBusiness(usuario, id);
			this.errorService.Log("login controller post: /empresa/{id}) Exitoso: " + usuario.getUsuario().getEmail());
			return new Representacion<LoginResponse>(HttpStatus.OK.value(), response);
		} catch (Exception ex) {
			logger.log(Level.ERROR, "login controller /empresa/{id} Error:", ex.getMessage(), ex.getStackTrace());
			this.errorService
					.Log("login controller @PostMapping(\"/login\")  Error: " + ex.getMessage() , " StackTrace: " + ex.getStackTrace());
			throw new NotAuthorizedException("Ingreso no autorizado.");
		}
	}

}
