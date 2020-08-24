package courier.uy.core.services.implementations;

import java.util.List;
import java.util.Optional;

import courier.uy.core.db.EmpaquesDAO;
import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.services.interfaces.IEmpaquesService;
import courier.uy.core.services.interfaces.IErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpaquesService implements IEmpaquesService {

	@Autowired
	private EmpaquesDAO empaquesDAO;
	@Autowired
	private IErrorService errorService;

	public EmpaquesService() {
	}

	public EmpaquesService(EmpaquesDAO empaquesDAO, IErrorService errorService) {
		this.empaquesDAO = empaquesDAO;
		this.errorService = errorService;
	}

	public IErrorService getErrorService() {
		return this.errorService;
	}

	public void setErrorService(IErrorService errorService) {
		this.errorService = errorService;
	}

	public EmpaquesDAO getEmpaquesDAO() {
		return this.empaquesDAO;
	}

	public void setEmpaquesDAO(EmpaquesDAO empaquesDAO) {
		this.empaquesDAO = empaquesDAO;
	}

	public EmpaquesService empaquesDAO(EmpaquesDAO empaquesDAO) {
		this.empaquesDAO = empaquesDAO;
		return this;
	}

	@Override
	public Optional<Empaque> obtenerEmpaque(String idEmpaque, UsuarioEmpresa ue) throws ServiceException {
		Optional<Empaque> empaqueOptional = null;
		try {
			empaqueOptional = empaquesDAO.findById(idEmpaque, ue);

		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el Empaque. Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
		if (!empaqueOptional.isPresent())
			throw new ServiceException("No existe el Empaque solicitado o no tiene acceso al mismo");

		return empaqueOptional;
	}

	@Override
	public PaginadoResponse<List<Empaque>> obtenerEmpaques(PaginadoRequest paginado, UsuarioEmpresa ue)
			throws ServiceException {

		try {
			return this.empaquesDAO.getAll(paginado, ue);
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Empaques. Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

}
