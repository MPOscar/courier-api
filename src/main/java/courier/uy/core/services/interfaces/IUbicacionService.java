package courier.uy.core.services.interfaces;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import courier.uy.core.entity.Ubicacion;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;

public interface IUbicacionService {

	Ubicacion insert(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	Ubicacion upsert(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	Ubicacion delete(Ubicacion ubicacion, UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	List<Ubicacion> getAll(MultivaluedMap<String, String> parametros, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException;

	public Ubicacion getUbicacion(String codigo, UsuarioEmpresa usuarioEmpresa) throws ServiceException;

}
