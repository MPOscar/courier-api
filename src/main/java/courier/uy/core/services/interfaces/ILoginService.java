package courier.uy.core.services.interfaces;

import java.util.Optional;

import courier.uy.core.resources.dto.UsuarioBasic;
import courier.uy.core.entity.Usuario;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.LoginResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;

public interface ILoginService {
	public LoginResponse LoginBusiness(UsuarioPrincipal user, String id) throws ServiceException, Exception;

	LoginResponse LoginBasic(UsuarioBasic usuario) throws ServiceException, Exception;

	LoginResponse Login(Usuario usuario, Optional<String> id) throws ServiceException, Exception;
}
