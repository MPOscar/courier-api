package courier.uy.core.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.ListaDeVenta;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.entity.Grupo;
import courier.uy.core.resources.dto.PaginadoRequest;

import courier.uy.core.exceptions.ModelException;
import courier.uy.core.repository.IListaDeVentaRepository;
import courier.uy.core.resources.dto.ListaVentaBasic;
import courier.uy.core.resources.dto.PaginadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class ListasDeVentaDAO {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private EmpresasDAO empresasDAO;

	@Autowired
    IListaDeVentaRepository listaDeVentaRepository;

	private final MongoOperations mongoOperations;

	public ListasDeVentaDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public ListaDeVenta findById(String id) {
		return listaDeVentaRepository.findById(id).get();
	}

	public ListaDeVenta save(ListaDeVenta toAdd) {
		toAdd.setFechaEdicion();
		toAdd = listaDeVentaRepository.save(toAdd);
		toAdd.setSId(toAdd.getId());
		return listaDeVentaRepository.save(toAdd);
	}

	public Optional<ListaDeVenta> findById(String idListaVenta, String idEmpresa) {
		Optional<ListaDeVenta> listaDeVenta = listaDeVentaRepository.findById(idListaVenta);
		return listaDeVenta;

	}

	public PaginadoResponse<List<Producto>> obtenerProductosListaVenta(PaginadoRequest paginado, String idListaVenta,
                                                                       String idEmpresa) throws ModelException {
		ListaDeVenta listaDeVenta = listaDeVentaRepository.findById(idListaVenta).get();
		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<>();
		paginadoResponse.getElementos().addAll(listaDeVenta.getProductos());
		return paginadoResponse;
	}

	public List<ListaDeVenta> getAll() {
		return listaDeVentaRepository.findAll();
	}

	public List<ListaDeVenta> findByKey(String key, String value) {
		Query query = new Query();
		query.addCriteria(Criteria.where(key).is(value).andOperator(Criteria.where("eliminado").is(false)));
		List<ListaDeVenta> listasVenta = mongoOperations.find(query, ListaDeVenta.class);
		return listasVenta;
	}

	/**
	 * <p>
	 * Devuelve el listado de {@link ListaDeVenta} de la {@link Company} pasada por
	 * Parámetros.
	 * </p>
	 * 
	 * <p>
	 * Se debe cumplir al menos uno de los siguientes criterios para determinar si
	 * se devuelve una {@link ListaDeVenta} en el listado:
	 * </p>
	 * <p>
	 * - La {@link ListaDeVenta} pertenece a la {@link Company} actualmente logueada
	 * </p>
	 * <p>
	 * - La {@link ListaDeVenta} tiene asociada a la {@link Company} logueada, ya
	 * sea de forma directa o mediante uno o varios {@link Grupo}, al menos un
	 * {@link Producto}
	 * </p>
	 * 
	 * 
	 * @param paginado
	 * @param idEmpresa
	 * @param ue
	 * @return
	 * @throws ModelException
	 */
	public PaginadoResponse<List<ListaDeVenta>> obtenerListaVentaEmpresa(PaginadoRequest paginado, String idEmpresa,
			UsuarioEmpresa ue) throws ModelException {
		Company empresa = ue.getEmpresa();
		String sid = empresa.getSId();
		Set<String> gruposEmpresa = empresa.getSempresaGrupos();
		List<ListaDeVenta> listaDeVentas = listaDeVentaRepository.findAllBySempresaAndSempresasInOrSgruposIn(idEmpresa, empresa.getSId(), empresa.getSempresaGrupos());
		PaginadoResponse<List<ListaDeVenta>> paginadoResponse = new PaginadoResponse<List<ListaDeVenta>>();
		paginadoResponse.setElementos(listaDeVentas);
		return paginadoResponse;
	}

	/**
	 * <p>
	 * Devuelve el listado de {@link ListaDeVenta} de la {@link Company} pasada por
	 * Parámetros.
	 * </p>
	 *
	 * <p>
	 * Se debe cumplir al menos uno de los siguientes criterios para determinar si
	 * se devuelve una {@link ListaDeVenta} en el listado:
	 * </p>
	 * <p>
	 * - La {@link ListaDeVenta} pertenece a la {@link Company} actualmente logueada
	 * </p>
	 * <p>
	 * - La {@link ListaDeVenta} tiene asociada a la {@link Company} logueada, ya
	 * sea de forma directa o mediante uno o varios {@link Grupo}, al menos un
	 * {@link Producto}
	 * </p>
	 *
	 *
	 * @param paginado
	 * @param idEmpresa
	 * @param ue
	 * @return
	 * @throws ModelException
	 */
	public PaginadoResponse<List<ListaVentaBasic>> obtenerListaVentaEmpresaId(PaginadoRequest paginado, String idEmpresa,
                                                                              UsuarioEmpresa ue) throws ModelException {
		Company empresa = ue.getEmpresa();
		List<ListaVentaBasic> listasVentaBasic = new ArrayList<>();
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(idEmpresa).andOperator(Criteria.where("eliminado").is(false)).orOperator(Criteria.where("sempresas").in(empresa.getId()), Criteria.where("sgrupos").in(empresa.getSempresaGrupos())));
		List<ListaDeVenta> listasVenta = mongoOperations.find(query, ListaDeVenta.class);

		for (ListaDeVenta listaDeVenta : listasVenta) {
			String id = listaDeVenta.getId();
			String nombre = listaDeVenta.getNombre();
			String codigo = listaDeVenta.getUbicacion().getCodigo();
			String descripcion = listaDeVenta.getDescripcion();
			ListaVentaBasic listaVentaBasic = new ListaVentaBasic(id, nombre, descripcion, codigo);
			listasVentaBasic.add(listaVentaBasic);
		}
		PaginadoResponse<List<ListaVentaBasic>> paginadoResponse = new PaginadoResponse<List<ListaVentaBasic>>();
		paginadoResponse.setElementos(listasVentaBasic);
		return paginadoResponse;
	}

	public PaginadoResponse<List<ListaVentaBasic>> obtenerListasVentaEmpresaIdCatalogo(String idEmpresa) {
		List<ListaVentaBasic> listasVentaBasic = new ArrayList<>();

		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(idEmpresa).andOperator(Criteria.where("eliminado").is(false)));
		List<ListaDeVenta> listasVenta = mongoOperations.find(query, ListaDeVenta.class);

		for (ListaDeVenta lista : listasVenta) {
			String id = lista.getId();
			String nombre = lista.getNombre();
			String descripcion = lista.getDescripcion();
			ListaVentaBasic listaVentaBasic = new ListaVentaBasic(id, nombre, descripcion, "0");
			listasVentaBasic.add(listaVentaBasic);
		}
		PaginadoResponse<List<ListaVentaBasic>> paginadoResponse = new PaginadoResponse<List<ListaVentaBasic>>();
		paginadoResponse.setElementos(listasVentaBasic);
		return paginadoResponse;
	}

}
