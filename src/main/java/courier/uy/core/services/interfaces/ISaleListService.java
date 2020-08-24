package courier.uy.core.services.interfaces;

import java.util.List;
import java.util.Set;

import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.resources.dto.ListaVentaBasic;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.exceptions.ServiceException;

public interface ISaleListService {
	public ListaDeVenta Create(ListaDeVenta lv, UsuarioEmpresa ue) throws ServiceException;

	public ListaDeVenta Modify(ListaDeVenta lv, UsuarioEmpresa u) throws ServiceException;

	public void Delete(ListaDeVenta g, UsuarioEmpresa u) throws ServiceException;

	public Set<ListaDeVenta> GetAllFromProvider(UsuarioPrincipal user) throws ServiceException, Exception;

	public PaginadoResponse<List<ListaVentaBasic>> GetAllSaleListFromProvider(UsuarioPrincipal user) throws ServiceException, Exception;

	public PaginadoResponse<List<ListaDeVenta>> GetAllFromProviderForSupermarket(PaginadoRequest paginado,
                                                                                 UsuarioPrincipal user, String providerId) throws ServiceException, Exception;

	public PaginadoResponse<List<ListaVentaBasic>> GetAllFromProviderForSupermarketPedido(PaginadoRequest paginado,
																						  UsuarioPrincipal user, String providerId) throws ServiceException, Exception;
	public ListaDeVenta GetById(String idListaVenta, String idEmpresa) throws ServiceException;

	public void agregarListaVentaVisibilidad(String listaDeVentaId, Producto producto);

	public void listaVentaVisibilidadDeleteAll(String listaDeVentaId, String productoId);
}
