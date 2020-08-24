package courier.uy.core.services.interfaces;

import java.util.List;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;

public interface ICategoryService {
	public void Create(Categoria c, UsuarioEmpresa ue) throws ServiceException;

	public void ChangeProducts(Categoria c, UsuarioEmpresa ue) throws ServiceException;

	public PaginadoResponse<List<Categoria>> GetAllFromBusiness(PaginadoRequest pr, UsuarioEmpresa ue)
			throws ServiceException;

	public PaginadoResponse<List<Categoria>> GetAllFromProvider(PaginadoRequest pr, String providerId)
			throws ServiceException;

	public Categoria GetCategoryById(String id) throws ServiceException;

	public Categoria GetCategoryById(String id, UsuarioEmpresa ue) throws ServiceException;

	public void Delete(Categoria c, UsuarioEmpresa u) throws ServiceException;
}
