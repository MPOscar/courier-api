package courier.uy.core.services.interfaces;

import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import courier.uy.core.resources.dto.*;
import courier.uy.core.utils.poiji.ExcelRNCliente;
import courier.uy.core.utils.poiji.ExcelRNProducto;
import courier.uy.core.entity.Grupo;
import org.apache.poi.ss.usermodel.Workbook;

import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.ProductoAccion;
import courier.uy.core.entity.UsuarioEmpresa;

public interface IProductService {

	/**
	 * 
	 * Este método inserta o actualiza según sea el caso los {@link Producto}
	 * pasados por parámetros siguiendo la siguiente lógica de precedencia en las
	 * acciones a ejecutar:
	 * <p>
	 * <p>
	 * - Primero si se solicita se eliminan todos los {@link Producto}
	 * </p>
	 * <p>
	 * - Luego si se solicita se eliminan los {@link Producto} existentes
	 * </p>
	 * <p>
	 * - Finalmente si se solicita se actualizan los {@link Producto} existentes
	 * </p>
	 * 
	 * </p>
	 * 
	 * 
	 * @param productos
	 * @param user
	 * @param updateExistent
	 * @param deleteExistent
	 * @param deleteAll
	 * @throws ServiceException
	 */
	void InsertExcel(List<ExcelProduct> productos, UsuarioPrincipal user, Boolean updateExistent, Boolean deleteExistent,
                     Boolean deleteAll) throws ServiceException;

	void InsertExcelRN(List<ExcelProduct> productos, Company empresa, Boolean updateExistent, Boolean deleteExistent,
                       Boolean deleteAll) throws ServiceException;

	void Delete(String id, UsuarioPrincipal user) throws ServiceException;

	void deleteProductsArray(EliminarProductos eliminarProductos, UsuarioPrincipal user) throws ServiceException;

	List<String> obtenerTodosLosProductos(UsuarioPrincipal user);

	Producto Insert(Producto p, UsuarioPrincipal ue) throws ServiceException;

	List<Producto> GetAll(UsuarioEmpresa ue, MultivaluedMap<String, String> parametros);

	/**
	 * Devuelve el listado de {@link Producto} disponible para la {@link Company}
	 * con el id pasado por parámetros
	 * 
	 * <p>
	 * Se considera que un {@link Producto}( no eliminado ) está disponible cuando
	 * se cumpla una de las siguientes condiciones:
	 * </p>
	 * <p>
	 * - Tiene visibilidad directa la {@link Company}
	 * </p>
	 * <p>
	 * - Tiene visibilidad la {@link Company} mediante un {@link Grupo} asociado al
	 * {@link Producto}
	 * </p>
	 * 
	 * @param ue
	 * @param businessId
	 * @return
	 * @throws ServiceException
	 */
	Set<Producto> obtenerProductosVisiblesEmpresa(UsuarioEmpresa ue, String businessId) throws ServiceException;

	/**
	 * Devuelve el listado de {@link Producto} disponible para la {@link Company}
	 * con el id pasado por parámetros
	 * 
	 * <p>
	 * Se considera que un {@link Producto}( no eliminado ) está disponible cuando
	 * se cumpla una de las siguientes condiciones:
	 * </p>
	 * <p>
	 * - Tiene visibilidad directa la {@link Company}
	 * </p>
	 * <p>
	 * - Tiene visibilidad la {@link Company} mediante un {@link Grupo} asociado al
	 * {@link Producto}
	 * 
	 * - La {@link Company} pasada por parámetros es la dueña del Producto
	 * </p>
	 * 
	 * @param pr
	 * @param ue
	 * @param businessId
	 * @return
	 * @throws ServiceException
	 */

	public PaginadoResponse<List<Producto>> obtenerProductosVisiblesEmpresa(PaginadoRequest pr, UsuarioEmpresa ue,
                                                                            String businessId) throws ServiceException;

	public PaginadoResponse<List<Producto>> obtenerProductosVisiblesParaEmpresaSeleccionada(PaginadoRequest pr,
			UsuarioEmpresa ue, String businessId) throws ServiceException;

	public PaginadoResponse<List<Producto>> obtenerProductosEmpresa(PaginadoRequest pr, UsuarioEmpresa ue,
			String businessId) throws ServiceException;

	public List<String> obtenerProductosEmpresaMarcas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public List<String> obtenerProductosEmpresaDivisiones(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public List<String> obtenerProductosEmpresaLineas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public List<String> obtenerProductosEmpresaProveedorMarcas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public List<String> obtenerProductosEmpresaProveedorDivisiones(PaginadoRequest pr, UsuarioEmpresa ue, String businessId) throws ServiceException;

	public List<String> obtenerProductosEmpresaProveedorLineas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public List<Set<String>> obtenerProductosEmpresaProveedorFiltros(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException;

	public Producto Modify(Producto p, UsuarioPrincipal usuario) throws ServiceException;

	public void ChangeVisibility(Producto p, UsuarioEmpresa ue) throws ServiceException;

	public void ChangeMassiveVisibility(VisibilityRequest visibilityRequest, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException;

	public PaginadoResponse<List<Producto>> GetVisibleForBusiness(PaginadoRequest pr, String businessId,
			UsuarioEmpresa ue) throws ServiceException;

	public void ChangeBusinessVisibility(VisibilityRequest visibilityRequest, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException;

	public List<ProductoAccion> GetNotAcknowledgedActions(String businessId, String providerId, UsuarioEmpresa ue);

	public void SetAcknowledge(AcknowledgeRequest acknowledgeRequest, UsuarioEmpresa ue) throws ServiceException;

	public Producto GetSingleProduct(UsuarioEmpresa usuarioEmpresa, String businessId, String productId)
			throws ServiceException;

	public Set<Producto> GetAllVisible(UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	public String exportarExcel(PaginadoRequest pr, UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	public String uploadErrorExcel(Workbook wb, Long empresaRut, String idUsuarioEmpresa, boolean isProducts);

	public Workbook getErrorExcel(List<ExcelProduct> productList);

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
	public Set<Producto> GetVisibleByBussinesOnSaleList(ListaDeVenta lv, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException;

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
	public Set<Producto> GetVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException;


	/**
	 * Devuelve los {@link Grupo} que tienen visibilidad sobre el {@link Producto}
	 * acorde al siguiente criterio
	 * 
	 * <p>
	 * - Si la {@link Company} asociada al {@link UsuarioEmpresa} pasado por
	 * parámetros es la dueña del {@link Producto} se muestran todos los
	 * {@link Grupo}
	 * </p>
	 * <p>
	 * - Si la {@link Company} asociada al {@link UsuarioEmpresa} pasado por
	 * parámetros <b>NO</b> es la dueña del {@link Producto} se verifica su
	 * visibilidad y se devuelve solo el Grupo donde se le otorgue visibilidad a la
	 * {@link Company} en cuestión
	 * </p>
	 * 
	 * 
	 * @param idProducto
	 * @param usuarioEmpresa
	 * @return
	 * @throws ServiceException
	 */
	public PaginadoResponse<List<Grupo>> obtenerGruposConVisibilidad(PaginadoRequest pr, String idProducto,
			UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	/**
	 * Devuelve las {@link Company} que tienen visibilidad sobre el {@link Producto}
	 * acorde al siguiente criterio
	 * 
	 * <p>
	 * - Si la {@link Company} asociada al {@link UsuarioEmpresa} pasado por
	 * parámetros es la dueña del {@link Producto} se muestran todas las
	 * {@link Company}
	 * </p>
	 * <p>
	 * - Si la {@link Company} asociada al {@link UsuarioEmpresa} pasado por
	 * parámetros <b>NO</b> es la dueña del {@link Producto} se verifica su
	 * visibilidad y se devuelve solo la {@link Company} asociada al
	 * {@link UsuarioEmpresa} si tiene visibilidad
	 * </p>
	 * 
	 * @param idProducto
	 * @param usuarioEmpresa
	 * @return
	 * @throws ServiceException
	 */
	public PaginadoResponse<Set<Company>> obtenerEmpresasConVisibilidad(PaginadoRequest pr, String idProducto,
                                                                        UsuarioEmpresa usuarioEmpresa) throws ServiceException;

	/**
	 * 
	 * Método para actualizar la referencia a las imágenes disponibles en el
	 * <b>Bucket de AWS</b> en los {@link Producto} pertenecientes a la
	 * {@link Company} pasada por parámetros
	 * 
	 * @param empresa
	 * @return
	 */
	public String actualizarURLImagenes(String bucket, Company empresa);

	/**
	 * 
	 * Método al que se le pasará la lista de {@link ExcelRNCliente} generados a
	 * partir de la Plantilla exportada desde RN Clásico. En cada carga de la
	 * Plantilla se eliminarán todos los {@link Grupo} existentes previamente para
	 * asegurar que se llegue a un estado igual al del Catalogo RN Clasico.
	 * 
	 * @param listaClientes
	 * @param ue
	 * @throws ServiceException
	 */
	public void crearClientesExcelRN(List<ExcelRNCliente> listaClientes, UsuarioEmpresa ue) throws ServiceException;

	/**
	 * 
	 * Método encargado de eliminar los {@link Producto} encontrados en el Catalogo
	 * de la {@link Company} asociada al {@link UsuarioJwt} pasado por parámetros.
	 * Una vez eliminados los {@link Producto} se procede a insertar los
	 * {@link Producto} pasados por parametros
	 * 
	 * @param listaProductos
	 * @param usuario
	 * @throws ServiceException
	 */
	public List<ExcelProduct> crearProductosRN(List<ExcelRNProducto> listaProductos, UsuarioPrincipal usuario)
			throws ServiceException;

	/**
	 * 
	 * Método encargado de eliminar los {@link Producto} encontrados en el Catalogo
	 * de la {@link Company} pasada por parámetros. Una vez eliminados los
	 * {@link Producto} se procede a insertar los {@link Producto} pasados por
	 * parametros
	 * 
	 * @param listaProductos
	 * @param usuario
	 * @param empresa
	 * @throws ServiceException
	 */
	public List<ExcelProduct> crearProductosRN(List<ExcelRNProducto> listaProductos, UsuarioPrincipal usuario,
			Company empresa) throws ServiceException;

	/**
	 * 
	 * Devuelve un {@link List<  ExcelCliente  >} con el listado de los
	 * {@link ExcelCliente} que fueron cargados de Excel de Rondanet Clasico
	 * 
	 * @param listaClientes
	 * @param usuario
	 * @param empresa
	 * @return
	 * @throws ServiceException
	 */
	public List<ExcelCliente> crearClientesRN(List<ExcelRNCliente> listaClientes, UsuarioPrincipal usuario, Company empresa)
			throws ServiceException;

	public boolean visibilidadPrivadaEnListaVenta(List<ExcelRNProducto> listaProductos, String cpp);

}
