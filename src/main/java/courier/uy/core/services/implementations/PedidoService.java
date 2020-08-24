package courier.uy.core.services.implementations;

import courier.uy.CourierConfiguration;
import courier.uy.core.db.*;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.services.interfaces.IPedidoService;
import courier.uy.core.utils.S3FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PedidoService implements IPedidoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PedidoService.class);
	private CourierConfiguration configuration;

	@Autowired
	private ProductosDAO productsRepository;
	@Autowired
	private EmpresasDAO businessesRepository;
	@Autowired
	private CategoriasDAO categoriesRepository;
	@Autowired
	private PresentacionesDAO presentationsRepository;
	@Autowired
	private EmpaquesDAO packsRepository;
	@Autowired
	private GruposDAO groupsRepository;
	@Autowired
	private PalletsDAO palletsRepository;
	@Autowired
	private ProductosAccionesDAO productActionsRepository;
	@Autowired
	private ParamsDAO paramsDAO;
	@Autowired
	private ListasDeVentaDAO listasDeVentaDAO;

	private S3FileManager s3FileManager;

	public PedidoService(ProductosDAO productosDao, CategoriasDAO categoriasDAO, PresentacionesDAO presentacionesDAO,
						 EmpaquesDAO empaquesDAO, EmpresasDAO empresasDAO, GruposDAO gruposDAO, PalletsDAO palletsDAO,
						 ProductosAccionesDAO productosAccionesDAO, ParamsDAO paramsDAO, CourierConfiguration configuration, S3FileManager s3FileManager) {
		this.productsRepository = productosDao;
		this.categoriesRepository = categoriasDAO;
		this.presentationsRepository = presentacionesDAO;
		this.packsRepository = empaquesDAO;
		this.businessesRepository = empresasDAO;
		this.groupsRepository = gruposDAO;
		this.palletsRepository = palletsDAO;
		this.productActionsRepository = productosAccionesDAO;
		this.paramsDAO = paramsDAO;
		this.configuration = configuration;
		this.s3FileManager = s3FileManager;
	}

	@Override
	public List<String> obtenerProductosEmpresaMarcas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = businessId.equals("0") ? ue.getEmpresa() : businessesRepository.findById(businessId);
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaMarcas(pr, businessId, empresa);
			List<String> marcas = new ArrayList<>();
			for (Producto producto : productos) {
				marcas.add(producto.getMarca());
			}
			return marcas;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public HashMap<String, Integer> GetDivisionesVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa, PaginadoRequest paginadoRequest)
			throws ServiceException {
		try {
			ListaDeVenta listaDeVenta = this.listasDeVentaDAO.findById(listaDeVentaId);
			List<String> listDivisiones = this.productsRepository.getVisibleByBussinesOnSaleListPedido(usuarioEmpresa.getEmpresa(), listaDeVenta, paginadoRequest);
			HashMap<String, Integer> divisiones = new HashMap<>();
			for (String division : listDivisiones) {
					if(divisiones.containsKey(division)){
						divisiones.put(division, divisiones.get(division) + 1);
					}else{
						divisiones.put(division, 1);
					}
			}
			return divisiones;
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public HashMap<String, Integer> GetMarcasVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa, PaginadoRequest paginadoRequest)
			throws ServiceException {
		try {
			ListaDeVenta listaDeVenta = this.listasDeVentaDAO.findById(listaDeVentaId);
			List<String> listMarcas = this.productsRepository.getMarcasVisibleByBussinesOnSaleListPedido(usuarioEmpresa.getEmpresa(), listaDeVenta, paginadoRequest);
			HashMap<String, Integer> marcas = new HashMap<>();
			for (String marca : listMarcas) {
				if(marcas.containsKey(marca)){
					marcas.put(marca, marcas.get(marca) + 1);
				}else{
					marcas.put(marca, 1);
				}
			}
			return marcas;
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public HashMap<String, Integer> GetLineasVisibleByBussinesOnSaleList(String listaDeVentaId, UsuarioEmpresa usuarioEmpresa, PaginadoRequest paginadoRequest)
			throws ServiceException {
		try {
			ListaDeVenta listaDeVenta = this.listasDeVentaDAO.findById(listaDeVentaId);
			List<String> listLineas = this.productsRepository.getLineasVisibleByBussinesOnSaleListPedido(usuarioEmpresa.getEmpresa(), listaDeVenta, paginadoRequest);
			HashMap<String, Integer> lineas = new HashMap<>();
			for (String linea : listLineas) {
				if(lineas.containsKey(linea)){
					lineas.put(linea, lineas.get(linea) + 1);
				}else{
					lineas.put(linea, 1);
				}
			}
			return lineas;
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public List<String> obtenerProductosEmpresaLineas(PaginadoRequest pr, UsuarioEmpresa ue, String businessId)
			throws ServiceException {
		try {
			Company empresa = !businessId.equals("0") ? businessesRepository.findById(businessId) : ue.getEmpresa();
			List<Producto> productos = this.productsRepository.obtenerProductosEmpresaLineas(pr, businessId, empresa);
			List<String> lineas = new ArrayList<>();
			for (Producto producto : productos) {
				lineas.add(producto.getCategoria().getPadre().getNombre());
			}
			return lineas;
		} catch (ModelException e) {
			throw new ServiceException("Ocurrió un error al obtener el listado de Productos visibles por la Empresa "
					+ ue.getEmpresa().getNombre() + ". Error: " + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	public Set<Producto> GetVisibleByBussinesOnSaleList(PaginadoRequest paginadoRequest, ListaDeVenta lv, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException, ModelException {
		Set<Producto> productos = new HashSet<>();
		this.productsRepository.getVisibleByBussinesOnSaleListPedido(paginadoRequest, usuarioEmpresa.getEmpresa(), lv);
		return productos;
	}

	@Override
	public List<Producto> GetVisibleByBussinesOnSaleList(PaginadoRequest paginadoRequest, String listaDeVentaId, UsuarioEmpresa usuarioEmpresa)
			throws ServiceException, ModelException {
		ListaDeVenta listaDeVenta = this.listasDeVentaDAO.findById(listaDeVentaId);
		List<Producto> responseProductos = this.productsRepository.getVisibleByBussinesOnSaleListPedido(paginadoRequest, usuarioEmpresa.getEmpresa(), listaDeVenta);
		return responseProductos;
	}

}
