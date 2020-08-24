package courier.uy.core.services.interfaces;

import java.util.List;
import java.util.Set;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.UsuarioPrincipal;

public interface IGroupService {
	public Grupo Create(Grupo g, UsuarioEmpresa ue) throws ServiceException;

	public Grupo Modify(Grupo g, UsuarioEmpresa u) throws ServiceException;

	public void Delete(Grupo g, UsuarioEmpresa u) throws ServiceException;

	public Grupo AddBusinesses(Grupo g, UsuarioEmpresa u, List<Company> empresas) throws ServiceException;

	public Grupo RemoveBusinesses(Grupo g, UsuarioEmpresa u, List<Company> empresas) throws ServiceException;

	public Set<Grupo> GetAllFromProvider(UsuarioPrincipal user) throws ServiceException, Exception;

	public Grupo GetGroupById(String id) throws ServiceException;
}
