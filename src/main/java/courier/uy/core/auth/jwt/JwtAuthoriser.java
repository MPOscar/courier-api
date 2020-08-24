package courier.uy.core.auth.jwt;

import java.util.Set;

import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.resources.dto.UsuarioJwt;
import courier.uy.core.entity.Rol;
import courier.uy.core.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Authorizer;

public class JwtAuthoriser implements Authorizer<UsuarioJwt> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthoriser.class);

	@Override
	public boolean authorize(UsuarioJwt user, String requiredRole) {
		//return true;
		if (user == null) {
			LOGGER.warn("msg=User object was null");
			return false;
		}

		if(requiredRole.equals(Roles.SYSTEM_ADMIN)) {
			Usuario u = user.getUsuario();
			if(u.esAdministradorSistema()) {
				return true;
			}
			return false;
		}
		
		if(requiredRole.equals(Roles.NEW_USER)) {
			return true;
		}		

		UsuarioEmpresa uEmpresa = user.getUsuarioEmpresa();
		if(uEmpresa == null) {
			LOGGER.warn("msg=Usuario Empresa object was null");
			return false;
		}
		Set<Rol> roles = uEmpresa.getRoles();
		for(Rol rol:roles) {
			if(rol.getRol().equals(requiredRole))
				return true;
		}
		/*String role = uEmpresa.getRol();
		if(requiredRole.equals(Roles.PROVIDER_ADMIN))
			return role.equals(requiredRole);
		else if(requiredRole.equals(Roles.PROVIDER_USER)) {
			return role.equals(requiredRole) || role.equals(Roles.PROVIDER_ADMIN);
		}
		else if(requiredRole.equals(Roles.SUPERMARKET_ADMIN))
			return role.equals(requiredRole);
		else if (requiredRole.equals(Roles.SUPERMARKET_USER))
			return role.equals(requiredRole) || role.equals(Roles.SUPERMARKET_ADMIN);*/
		return false;
	}
}