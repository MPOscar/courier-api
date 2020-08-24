package courier.uy.core.security;

import courier.uy.core.resources.dto.UsuarioPrincipal;
import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    UsuarioPrincipal getPrincipalAuth();
}
