package courier.uy.core.services.implementations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import courier.uy.core.db.*;
import courier.uy.core.entity.*;
import courier.uy.core.repository.IListaDeVentaVisibilidadRepository;
import courier.uy.core.services.interfaces.ISaleListService;
import courier.uy.core.resources.dto.ListaVentaBasic;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.PaginadoResponse;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import courier.uy.core.exceptions.ServiceException;

import javax.transaction.Transactional;

@Service
public class SaleListService implements ISaleListService {
	private ListasDeVentaDAO salesListRepository;
	private UsuariosDAO userRepository;
	private EmpresasDAO businessRepository;
	private ProductosDAO productsRepository;
	private GruposDAO groupsRepository;
	private ListaDeVentaVisibilidadEmpresaDAO listaDeVentaVisibilidadEmpresaDAO;
	private ListaDeVentaVisibilidadGrupoDAO listaDeVentaVisibilidadGrupoDAO;
	@Autowired
	private IListaDeVentaVisibilidadRepository listaDeVentaVisibilidadRepository;

	public SaleListService(ProductosDAO pDAO, UsuariosDAO usuariosDAO, EmpresasDAO businessDAO,
			ListasDeVentaDAO listaDAO, GruposDAO groupsDAO, ListaDeVentaVisibilidadEmpresaDAO listaDeVentaVisibilidadEmpresaDAO, ListaDeVentaVisibilidadGrupoDAO listaDeVentaVisibilidadGrupoDAO) {
		this.productsRepository = pDAO;
		this.salesListRepository = listaDAO;
		this.userRepository = usuariosDAO;
		this.businessRepository = businessDAO;
		this.groupsRepository = groupsDAO;
		this.listaDeVentaVisibilidadEmpresaDAO = listaDeVentaVisibilidadEmpresaDAO;
		this.listaDeVentaVisibilidadGrupoDAO = listaDeVentaVisibilidadGrupoDAO;
	}

	@Override
	public ListaDeVenta Create(ListaDeVenta lv, UsuarioEmpresa ue) throws ServiceException {
		this.userRepository.existeUsuario(ue.getUsuario());
		this.businessRepository.existeEmpresa(ue.getEmpresa().getId());
		this.nameIsNotRepeated(lv);
		Company e = this.businessRepository.findById(ue.getEmpresa().getId());
		if (e.getValidado() && !e.getEliminado()) {
			ListaDeVenta lvRepo = new ListaDeVenta(lv);
			lvRepo.setEmpresa(ue.getEmpresa());
			lvRepo.setSempresa(ue.getEmpresa().getId());
			lvRepo.setFechaCreacion();
			lvRepo = salesListRepository.save(lvRepo);
			if (lv.getEmpresas() != null) {
				Set<Company> saleListBusinesses = new HashSet<Company>();
				Set<String> saleListsBusinesses = new HashSet<String>();
				for (Company b : lv.getEmpresas()) {
					this.businessRepository.existeEmpresa(b.getId());
					Company business = this.businessRepository.findById(b.getId());
					saleListBusinesses.add(business);
					saleListsBusinesses.add(business.getId());
				}
				lvRepo.setEmpresas(saleListBusinesses);
				lvRepo.setSempresas(saleListsBusinesses);
			}
			if (lv.getGrupos() != null) {
				Set<Grupo> saleListGroups = new HashSet<Grupo>();
				Set<String> saleListsGroups = new HashSet<String>();
				for (Grupo g : lv.getGrupos()) {
					Grupo group = this.groupsRepository.findById(g.getId());
					saleListGroups.add(group);
					saleListsGroups.add(group.getId());
				}
				lvRepo.setGrupos(saleListGroups);
				lvRepo.setSgrupos(saleListsGroups);
			}
			if (lv.getProductos() != null) {
				Set<Producto> saleListProducts = new HashSet<Producto>();
				Set<String> saleListsProducts = new HashSet<String>();
				Set<ListaDeVentaVisibilidad> listaDeVentaVisibilidadSet = new HashSet<ListaDeVentaVisibilidad>();
				Set<String> slistaDeVentaVisibilidadSet = new HashSet<String>();
				for (Producto p : lv.getProductos()) {
					Producto product = this.productsRepository.findById(p.getId());
					saleListProducts.add(product);
					saleListsProducts.add(product.getId());
					ListaDeVentaVisibilidad listaDeVentaVisibilidad = new ListaDeVentaVisibilidad();
					listaDeVentaVisibilidad.setListaDeVenta(lvRepo);
					listaDeVentaVisibilidad.setSlistaDeVenta(lvRepo.getId());
					listaDeVentaVisibilidad.setProducto(product);
					listaDeVentaVisibilidad.setSproducto(product.getId());
					listaDeVentaVisibilidad = listaDeVentaVisibilidadRepository.save(listaDeVentaVisibilidad);
					listaDeVentaVisibilidad.setSId(listaDeVentaVisibilidad.getId());
					listaDeVentaVisibilidadRepository.save(listaDeVentaVisibilidad);
					listaDeVentaVisibilidadSet.add(listaDeVentaVisibilidad);
					slistaDeVentaVisibilidadSet.add(listaDeVentaVisibilidad.getId());
				}
				lvRepo.setProductos(saleListProducts);
				lvRepo.setSproductos(saleListsProducts);
				lvRepo.setListaDeVentaProductosVisibilidad(listaDeVentaVisibilidadSet);
				lvRepo.setSlistaDeVentaProductosVisibilidad(slistaDeVentaVisibilidadSet);
			}
			return this.salesListRepository.save(lvRepo);
		} else {
			throw new ServiceException("Antes de crear una lista un administrador debe validar esta empresa");
		}

	}

	@Override
	public ListaDeVenta Modify(ListaDeVenta g, UsuarioEmpresa ue) throws ServiceException {
		ListaDeVenta saleList = this.salesListRepository.findById(g.getId());
		if (saleList.getEmpresa().getId().equals(ue.getEmpresa().getId())) {
			if (!g.getNombre().equals(saleList.getNombre())) {
				saleList.setNombre(g.getNombre());
				nameIsNotRepeated(saleList);
			}
			saleList.setDescripcion(g.getDescripcion());

			if (g.getEmpresas() != null) {
				Set<Company> saleListBusinesses = new HashSet<Company>();
				Set<String> saleListsBusinesses = new HashSet<String>();
				for (Company b : g.getEmpresas()) {
					this.businessRepository.existeEmpresa(b.getId());
					Company business = this.businessRepository.findById(b.getId());
					saleListBusinesses.add(business);
					saleListsBusinesses.add(business.getId());
				}
				saleList.setEmpresas(saleListBusinesses);
				saleList.setSempresas(saleListsBusinesses);
			}else{
				saleList.setEmpresas(new HashSet<>());
				saleList.setSempresas(new HashSet<>());
			}

			if (g.getGrupos() != null) {
				Set<Grupo> saleListGroups = new HashSet<Grupo>();
				Set<String> saleListsGroups = new HashSet<String>();
				for (Grupo grupo : g.getGrupos()) {
					Grupo group = this.groupsRepository.findById(grupo.getId());
					saleListGroups.add(group);
					saleListsGroups.add(group.getId());
				}
				saleList.setGrupos(saleListGroups);
				saleList.setSgrupos(saleListsGroups);
			}

			if (g.getProductos() != null) {
				Set<Producto> saleListProducts = new HashSet<Producto>();
				Set<String> saleListsProducts = new HashSet<String>();
				Set<ListaDeVentaVisibilidad> listaDeVentaVisibilidadSet = new HashSet<ListaDeVentaVisibilidad>();
				Set<String> slistaDeVentaVisibilidadSet = new HashSet<String>();
				for (Producto p : g.getProductos()) {
					Producto product = this.productsRepository.findById(p.getId());
					saleListProducts.add(product);
					saleListsProducts.add(product.getId());
					ListaDeVentaVisibilidad listaDeVentaVisibilidad = new ListaDeVentaVisibilidad();
					listaDeVentaVisibilidad.setListaDeVenta(saleList);
					listaDeVentaVisibilidad.setSlistaDeVenta(saleList.getId());
					listaDeVentaVisibilidad.setProducto(product);
					listaDeVentaVisibilidad.setSproducto(product.getId());
					listaDeVentaVisibilidad = listaDeVentaVisibilidadRepository.save(listaDeVentaVisibilidad);
					listaDeVentaVisibilidad.setSId(listaDeVentaVisibilidad.getId());
					listaDeVentaVisibilidadRepository.save(listaDeVentaVisibilidad);
					listaDeVentaVisibilidadSet.add(listaDeVentaVisibilidad);
					slistaDeVentaVisibilidadSet.add(listaDeVentaVisibilidad.getId());
				}
				saleList.setProductos(saleListProducts);
				saleList.setSproductos(saleListsProducts);
				saleList.setListaDeVentaProductosVisibilidad(listaDeVentaVisibilidadSet);
				saleList.setSlistaDeVentaProductosVisibilidad(slistaDeVentaVisibilidadSet);
			}
			return this.salesListRepository.save(saleList);
		} else
			throw new ServiceException("Solo puedes editar un a lista de la empresa activa");

	}

	@Override
	public void Delete(ListaDeVenta g, UsuarioEmpresa ue) throws ServiceException {
		ListaDeVenta saleList = this.salesListRepository.findById(g.getId());
		if (saleList.getEmpresa().getId().equals(ue.getEmpresa().getId())) {
			saleList.setEmpresas(null);
			saleList.eliminar();
			this.salesListRepository.save(saleList);
		} else
			throw new ServiceException("Solo puedes editar un grupo de la empresa activa");
	}

	private void nameIsNotRepeated(ListaDeVenta lv) throws ServiceException {
		List<ListaDeVenta> listsWithName = this.salesListRepository.findByKey("nombre", lv.getNombre());
		for (ListaDeVenta saleList : listsWithName) {
			if (!saleList.getId().equals(lv.getId()))
				throw new ServiceException("Ya hay una lista de venta con este nombre");
		}
	}

	@Override
	public ListaDeVenta GetById(String idListaVenta, String idEmpresa) throws ServiceException {
		try {
			Optional<ListaDeVenta> lv = this.salesListRepository.findById(idListaVenta, idEmpresa);
			return lv.orElseThrow(() -> new ServiceException(
					"La Lista de Ventas no existe o no está disponible para la Empresa logueada"));
		} catch (Exception e) {
			throw new ServiceException("Error: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public Set<ListaDeVenta> GetAllFromProvider(UsuarioPrincipal user) throws ServiceException, Exception {
		// TODO Iteración 2. Implementar mediante consulta para determinar visibilidad
		// para Empresa
		UsuarioEmpresa ue = user.getUsuarioEmpresa();
		if (ue != null) {
			Company e = ue.getEmpresa();
			if (e != null) {
				Company bddEmp = this.businessRepository.findById(e.getId());
				return bddEmp.getListasDeVenta();
			} else {
				throw new ServiceException("No se encuentra esta empresa");
			}
		} else {
			throw new ServiceException("Este usuario no tiene acceso para esta empresa");
		}
	}

	@Override
	public PaginadoResponse<List<ListaVentaBasic>> GetAllSaleListFromProvider(UsuarioPrincipal user) throws ServiceException, Exception {
		UsuarioEmpresa ue = user.getUsuarioEmpresa();
		if (ue != null) {
			Company e = ue.getEmpresa();
			if (e != null) {
				return this.salesListRepository.obtenerListasVentaEmpresaIdCatalogo(e.getId());
			} else {
				throw new ServiceException("No se encuentra esta empresa");
			}
		} else {
			throw new ServiceException("Este usuario no tiene acceso para esta empresa");
		}
	}

	@Override
	public PaginadoResponse<List<ListaDeVenta>> GetAllFromProviderForSupermarket(PaginadoRequest paginado,
                                                                                 UsuarioPrincipal user, String providerId) throws ServiceException, Exception {
		return this.salesListRepository.obtenerListaVentaEmpresa(paginado, providerId, user.getUsuarioEmpresa());
	}

	@Override
	public PaginadoResponse<List<ListaVentaBasic>> GetAllFromProviderForSupermarketPedido(PaginadoRequest paginado,
																						  UsuarioPrincipal user, String providerId) throws ServiceException, Exception {
		return this.salesListRepository.obtenerListaVentaEmpresaId(paginado, providerId, user.getUsuarioEmpresa());
	}

	@Override
	public void agregarListaVentaVisibilidad(String listaDeVentaId, Producto producto){
		if(producto.getEmpresasConVisibilidad() != null && producto.getEmpresasConVisibilidad().size() > 0) {
			Set<Company> empresas = producto.getEmpresasConVisibilidad();
			for (Company empresa : empresas) {
				//ListaDeVentaVisibilidadEmpresa listaDeVentaVisibilidadEmpresa = new ListaDeVentaVisibilidadEmpresa(listaDeVentaId, empresa.getId(), producto.getId());
				//listaDeVentaVisibilidadEmpresaDAO.insert(listaDeVentaVisibilidadEmpresa);
			}
		}
		if(producto.getGruposConVisibilidad() != null && producto.getGruposConVisibilidad().size() > 0){
			Set<Grupo> grupos = producto.getGruposConVisibilidad();
			for (Grupo grupo: grupos) {
				//ListaDeVentaVisibilidadGrupo listaDeVentaVisibilidadGrupo = new ListaDeVentaVisibilidadGrupo(listaDeVentaId, grupo.getId(), producto.getId());
				//listaDeVentaVisibilidadGrupoDAO.insert(listaDeVentaVisibilidadGrupo);
			}
		}
	}

	public void listaVentaVisibilidadDeleteAll(String listaDeVentaId, String productoId){
		//listaDeVentaVisibilidadEmpresaDAO.deleteAll(listaDeVentaId, productoId);
		//listaDeVentaVisibilidadGrupoDAO.deleteAll(listaDeVentaId, productoId);
	}
}
