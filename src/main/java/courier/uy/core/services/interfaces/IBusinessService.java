package courier.uy.core.services.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import courier.uy.core.entity.Usuario;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;

import javax.transaction.Transactional;


public interface IBusinessService {
	/**
	 * Devuelve la {@link Company} con el <b>idEmpresa</b> pasado por parámetros
	 *
	 * @param idEmpresa
	 * @param usuario
	 * @return {@link Optional}<{@link Company}>
	 * @throws ServiceException
	 */
	public void UpdateSupermarket(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	public List<Company> GetUserBusinesses(UsuarioPrincipal user) throws ServiceException;

	public void Validate(String id) throws ServiceException;

	public void DeleteSupermarket(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	/**
	 *
	 * Devuelve un {@link PaginadoResponse} con el listado de las {@link Company}
	 * asociadas al {@link Usuario} pasado por parámetros
	 *
	 * @param pr
	 * @param user
	 * @param idEmpresa
	 * @return
	 * @throws ServiceException
	 */

	public PaginadoResponse<List<Company>> GetBusinesses(PaginadoRequest pr, String idEmpresa, UsuarioPrincipal user)
			throws ServiceException;

	public PaginadoResponse<List<Company>> GetBusinessesWithCatalogo(PaginadoRequest pr, String idEmpresa,
                                                                     UsuarioPrincipal user) throws ServiceException;

	public PaginadoResponse<List<Company>> GetBusinessesWithVisibility(PaginadoRequest pr, String idEmpresa,
                                                                       UsuarioPrincipal user) throws ServiceException;

	public PaginadoResponse<List<Company>> GetProveedores(PaginadoRequest pr, String idEmpresa, UsuarioPrincipal user)
			throws ServiceException;

	public Company Create(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	public void Update(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	public void CreateFromAdmin(UsuarioPrincipal usuario, Company empresa) throws ServiceException, Exception;

	public List<Company> GetAll() throws ServiceException;

	public void UpdateFromAdmin(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	public Company ActivateBusiness(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	public Company DeactivateBusiness(UsuarioPrincipal usuario, Company empresa) throws ServiceException;

	/**
	 *
	 * Devuelve un {@link PaginadoResponse} con el listado de las {@link Company}
	 * proveedoras de la {@link Company} pasada por parámetros
	 *
	 * @param pr
	 * @param usuario
	 * @param empresa
	 * @return
	 * @throws ServiceException
	 */
	public PaginadoResponse<List<Company>> getProviders(PaginadoRequest pr, UsuarioPrincipal usuario, Company empresa)
			throws ServiceException;

	/**
	 *
	 * Devuelve un {@link PaginadoResponse} con el listado de las {@link Company}
	 * con productos visibles para la {@link Company} pasada por parámetros
	 *
	 * @param pr
	 * @param usuario
	 * @param empresa
	 * @return
	 * @throws ServiceException
	 */
	public PaginadoResponse<List<Company>> getEmpresasConProductosVisibles(PaginadoRequest pr, UsuarioPrincipal usuario, Company empresa)
			throws ServiceException;

	@Transactional
	Optional<Company> obtenerEmpresa(String idEmpresa, UsuarioPrincipal usuario) throws ServiceException;

	@Transactional
	Set<Grupo> obtenerEmpresaGrupos(String idEmpresa, UsuarioPrincipal usuario) throws ServiceException;
}

