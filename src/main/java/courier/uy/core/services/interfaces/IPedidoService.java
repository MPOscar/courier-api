package courier.uy.core.services.interfaces;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface IPedidoService {


	public List<String> obtenerProductosEmpresaMarcas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public HashMap<String, Integer> GetDivisionesVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa, PaginadoRequest paginadoRequest)
			throws ServiceException;

	public HashMap<String, Integer> GetMarcasVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa, PaginadoRequest paginadoRequest)
			throws ServiceException;

	public HashMap<String, Integer> GetLineasVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa, PaginadoRequest paginadoRequest)
			throws ServiceException;

	public List<String> obtenerProductosEmpresaLineas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;


	/**
	 * Devuelve el listado de {@link Producto} asociados a la {@link ListaDeVenta}
	 * visibles para la {@link Company} presente en el {@link UsuarioEmpresa} pasado
	 * por parámetros
	 * 
	 * 
	 * @param lv
	 * @param usuarioEmpresa
	 * @return
	 * @throws ServiceException
	 */
	public Set<Producto> GetVisibleByBussinesOnSaleList(PaginadoRequest paginadoRequest, ListaDeVenta lv, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException, ModelException;

	/**
	 * Devuelve el listado de {@link Producto} asociados a la {@link ListaDeVenta}
	 * visibles para la {@link Company} presente en el {@link UsuarioEmpresa} pasado
	 * por parámetros
	 *
	 *
	 * @param listaDeVentaId
	 * @param usuarioEmpresa
	 * @return
	 * @throws ServiceException
	 */
	public List<Producto> GetVisibleByBussinesOnSaleList(PaginadoRequest paginadoRequest, String listaDeVentaId, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException, ModelException;

}
