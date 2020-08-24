package courier.uy.core.services.interfaces;

import java.util.List;
import java.util.Optional;

import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;

public interface IEmpaquesService {
	/**
	 * <p>
	 * Devuelve todos los {@link Empaque} disponibles para la {@link Empresa}
	 * referenciada en el objeto {@link UsuarioEmpresa}.
	 * </p>
	 * <p>
	 * En caso de que {@link UsuarioEmpresa} sea null se devuelven todos los
	 * {@link Empaque} existentes
	 * </p>
	 * 
	 * @param paginado
	 * @param ue
	 * @return {@link PaginadoRequest}
	 * @throws ServiceException
	 */
	public PaginadoResponse<List<Empaque>> obtenerEmpaques(PaginadoRequest paginado, UsuarioEmpresa ue)
			throws ServiceException;

	/**
	 * 
	 * <p>
	 * Devuelve el {@link Empaque} disponible para la {@link Empresa} referenciada
	 * en el objeto {@link UsuarioEmpresa}.
	 * </p>
	 * <p>
	 * En caso de que {@link UsuarioEmpresa} sea null se devuelve el {@link Empaque}
	 * independientemente de la {@link Empresa} a la que pertenece
	 * </p>
	 * 
	 * @param idEmpaque
	 * @param ue
	 * @return
	 * @throws ServiceException
	 */
	public Optional<Empaque> obtenerEmpaque(String idEmpaque, UsuarioEmpresa ue) throws ServiceException;

}
