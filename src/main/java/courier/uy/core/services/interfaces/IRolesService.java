package courier.uy.core.services.interfaces;

import java.util.List;

import courier.uy.core.entity.Rol;
import courier.uy.core.exceptions.ServiceException;

public interface IRolesService {

	public List<Rol> GetAll() throws ServiceException;

}
