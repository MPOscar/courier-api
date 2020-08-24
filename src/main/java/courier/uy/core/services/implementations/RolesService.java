package courier.uy.core.services.implementations;

import java.util.List;

import courier.uy.core.db.RolesDAO;
import courier.uy.core.entity.Rol;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.services.interfaces.IRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesService implements IRolesService {
	@Autowired
	private RolesDAO rolesRepository;

	public RolesService(RolesDAO rolesDAO) {
		this.rolesRepository = rolesDAO;
	}

	@Override
	public List<Rol> GetAll() throws ServiceException {
		return this.rolesRepository.getAll();
	}

}
