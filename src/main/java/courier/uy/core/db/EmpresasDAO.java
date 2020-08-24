package courier.uy.core.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.UsuarioPrincipal;
import courier.uy.core.entity.*;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.repository.IProductoRepository;
import courier.uy.core.repository.IUsuarioEmpresaRepository;
import courier.uy.core.resources.dto.PaginadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class EmpresasDAO {

	@Autowired
	private IEmpresaRepository empresaRepository;

	@Autowired
	private IUsuarioEmpresaRepository usuarioEmpresaRepository;

	@Autowired
	private IProductoRepository productoRepository;

	@Autowired
	private UsuariosDAO userRepository;

	private final MongoTemplate mongoTemplate;

	private final MongoOperations mongoOperations;

	@PersistenceContext
	private EntityManager em;

	public EmpresasDAO(MongoTemplate mongoTemplate, MongoOperations mongoOperations) {
		this.mongoTemplate = mongoTemplate;
		this.mongoOperations = mongoOperations;
	}

	public Company save(Company e) {
		e = empresaRepository.save(e);
		e.setSId(e.getId());
		empresaRepository.save(e);
		return e;
	}

	public Company findById(String id) {
		return this.findById(id, null);
	}

	public Company findById(String id, UsuarioEmpresa ue) {
		Optional<Company> empresa = empresaRepository.findById(id);
		return empresa.isPresent() ? empresa.get() : null;
	}

	public Optional<Company> findByRutEmpresa(long value) {
		Query query = new Query();
		List<Company> listaEmpresas = mongoOperations.find(query.addCriteria(Criteria.where("rut").is(value)), Company.class);

		if (listaEmpresas.size() != 0)
			return Optional.of(listaEmpresas.get(0));
		return Optional.ofNullable(null);
	}

	public Optional<Company> findByKey(String key, String value) {
		Query query = new Query();
		List<Company> listaEmpresas = mongoOperations.find(query.addCriteria(Criteria.where(key).is(value)), Company.class);

		if (listaEmpresas.size() != 0)
			return Optional.of(listaEmpresas.get(0));
		return Optional.ofNullable(null);
	}

	public List<Company> findAllByKey(String key, String value) {
		Query query = new Query();
		List<Company> listaEmpresas = mongoOperations.find(query.addCriteria(Criteria.where(key).is(value).andOperator(Criteria.where("eliminado").is(false))), Company.class);
		return listaEmpresas;
	}

	public List<Company> getAll() {
		return empresaRepository.findAll();
	}

	@Transactional
	public List<Grupo> getGruposDeLaEmpresa(long empresaId) {
		/*Empresa empresa = this.empresaRepository.findById(empresaId);
		List<Grupo> grupos = empresa.getGrupos();*/
		List<Grupo> grupos = new ArrayList<>();
		return grupos;
	}




	@Transactional
	public List<Company> getEmpresasUsuario(UsuarioPrincipal user) {
		List<Company> resultList = new ArrayList<>();
		Usuario usuario = userRepository.findById(user.getUsuario().getId());
		for (UsuarioEmpresa usuEmp : usuario.getUsuariosEmpresas()) {
			if (!usuEmp.getEliminado() && usuEmp.getEmpresa().getActivo() && !usuEmp.getEliminado()) {
				resultList.add(usuEmp.getEmpresa());
			}
		}
		return resultList;
	}

	public void empresaNoEstaRepetida(Company empresa) throws ServiceException {
		Optional<Company> existingRut = this.findByRutEmpresa(empresa.getRut());
		if (existingRut.isPresent() && existingRut.get().getId() != empresa.getId())
			throw new ServiceException("El rut " + empresa.getRut() + " ya existe");
	}

	public void existeEmpresa(String empresaId) throws ServiceException {
		Company e = this.findById(empresaId);
		if (e == null)
			throw new ServiceException("La empresa creadora de este grupo no existe");

	}

	public void update(Company e) {
		empresaRepository.save(e);
	}

	/**
	 * Devuelve el listado de las {@link Company} proveedoras de la {@link Company}
	 * pasada por parámetros. El criterio para establecer que una {@link Company} es
	 * Proveedor es el siguiente: La {@link Company} no está eliminada y tiene al
	 * menos un {@link Producto} no eliminado visible, ya sea directamente o
	 * mediante un {@link Grupo}.
	 * 
	 * 
	 * 
	 * @param
	 * @return Set de {@link Company}
	 */
	public Set<Company> getProveedoresEmpresa(Long idEmpresa) {
		/*TypedQuery<Empresa> query = em.createNamedQuery("getProveedoresEmpresa", Empresa.class);
		query.setParameter("target", idEmpresa);
		List<Empresa> listaProductos = query.getResultList();

		Set<Empresa> toReturn = new HashSet<Empresa>();
		toReturn.addAll(listaProductos);*/
		Set<Company> toReturn = new HashSet<>();
		return toReturn;
	}

	/**
	 * 
	 * Devuelve un {@link PaginadoResponse } con el listado de las {@link Company}
	 * proveedoras de la {@link Company} pasada por parámetros. El criterio para
	 * establecer que una {@link Company} es Proveedor es el siguiente: La
	 * {@link Company} no está eliminada y tiene al menos un {@link Producto} no
	 * eliminado visible, ya sea directamente o mediante un {@link Grupo}.
	 * 
	 * @param paginado
	 * @param idEmpresa
	 * @return
	 * @throws ModelException
	 */
	public PaginadoResponse<List<Company>> getEmpresas(PaginadoRequest paginado, String idEmpresa)
			throws ModelException {
		/*String sqlProveedores = paginado.getUriInfo().map(e -> e.getQueryParameters()).map(e -> {
			String sql = Utils.constructorDinamicoFiltros(Empresa.query_empresas, e);
			sql = Utils.constructorDinamicoOrdenamientos(sql, e);
			return sql;
		}).orElse(Empresa.query_empresas);
		TypedQuery<Empresa> query = (TypedQuery<Empresa>) em.createNativeQuery(sqlProveedores, Empresa.class);
		query.setParameter("target", idEmpresa);

		String sqlContarEmpresas = paginado.getUriInfo().map(e -> e.getQueryParameters())
				.map(e -> Utils.constructorDinamicoFiltros(Empresa.query_contarEmpresas, e))
				.orElse(Empresa.query_contarEmpresas);
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNativeQuery(sqlContarEmpresas);
		queryContar.setParameter("target", idEmpresa);

		return Utils.paginar(paginado, query, queryContar);*/

		Company EmpresaX = empresaRepository.findById(idEmpresa).get();
		Aggregation agg = newAggregation(
				match(Criteria.where("empresa").is("Sebamar"))
		);

		List<Producto> empresa = mongoTemplate.aggregate(agg, "Productos", Producto.class).getMappedResults();

		LookupOperation lookupOperation = LookupOperation.newLookup()
				.from("Empresa")
				.localField("empresa.$id")
				.foreignField("_id")
				.as("empresas");

		Company empresaE = empresaRepository.findById(idEmpresa).get();
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("empresa").is(empresaE)) , lookupOperation);
		List<Producto> results = mongoTemplate.aggregate(aggregation, "Producto", Producto.class).getMappedResults();

		String campo = "nombre";
		Query query = new Query().addCriteria(Criteria.where("emresa").is(empresaE)).with(Sort.by("descripcion")).limit(1);
		List<Producto> queryEmpresas = mongoTemplate.find(query, Producto.class);

		int page = (paginado.getPagina()).intValue() - 1;
		int size = (paginado.getCantidad()).intValue();
		Page<Company> empresas = empresaRepository.findAll(PageRequest.of(page, size));
		PaginadoResponse<List<Company>> paginadoResponse = new PaginadoResponse<List<Company>>();
		paginadoResponse.setElementos(empresas.toList());
		paginadoResponse.setCantidad(empresas.getTotalElements());
		return paginadoResponse;
	}

	public PaginadoResponse<List<Company>> getEmpresasConCatalogo(PaginadoRequest paginado, String idEmpresa)
			throws ModelException {
		/*String sqlProveedores = paginado.getUriInfo().map(e -> e.getQueryParameters()).map(e -> {
			String sql = Utils.constructorDinamicoFiltros(Empresa.query_empresasConCatalogo, e);
			sql = Utils.constructorDinamicoOrdenamientos(sql, e);
			return sql;
		}).orElse(Empresa.query_empresasConCatalogo);
		TypedQuery<Empresa> query = (TypedQuery<Empresa>) em.createNativeQuery(sqlProveedores, Empresa.class);
		query.setParameter("empresa", idEmpresa);

		String sqlContarEmpresas = paginado.getUriInfo().map(e -> e.getQueryParameters())
				.map(e -> Utils.constructorDinamicoFiltros(Empresa.query_contarEmpresasConCatalogo, e))
				.orElse(Empresa.query_contarEmpresasConCatalogo);
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNativeQuery(sqlContarEmpresas);
		queryContar.setParameter("empresa", idEmpresa);

		return Utils.paginar(paginado, query, queryContar);*/

		Aggregation agg = newAggregation(
				match(Criteria.where("_id").lt(10)),
				group("hosting").count().as("total"),
				project("total").and("hosting").previousOperation(),
				sort(Sort.Direction.DESC, "total")

		);

		Company empresa = empresaRepository.findById(idEmpresa).get();
		Set<String> empresaGrupos = empresa.getSempresaGrupos();
		String sIdEmpresa = empresa.getSId();
		Set<String> empresasConVisibilidad = new HashSet<>();
		empresasConVisibilidad.add(sIdEmpresa);

		Set<String> empresasConCatalogo = new HashSet<>();

		Aggregation empresasConProductosPublicosAggregation = Aggregation.newAggregation(match(Criteria.where("esPublico").is(true).and("sempresa").ne(sIdEmpresa)), group("sempresa"));

		List<Producto> empresasConProductosPublicos = mongoTemplate.aggregate(empresasConProductosPublicosAggregation, "Producto", Producto.class).getMappedResults();
		List<Producto> empresasConProductosPrivadosVisibles = productoRepository.findAllBySgruposConVisibilidadInOrSempresasConVisibilidadIn(empresaGrupos, sIdEmpresa);

		for (Producto producto: empresasConProductosPublicos) {
			empresasConCatalogo.add(producto.getId());
		}

		for (Producto producto: empresasConProductosPrivadosVisibles) {
			empresasConCatalogo.add(producto.getSempresa());
		}

		int page = (paginado.getPagina()).intValue() - 1;
		int size = (paginado.getCantidad()).intValue();
		PaginadoResponse<List<Company>> paginadoResponse = new PaginadoResponse<List<Company>>();
		List<Company> empresas = empresaRepository.findAllBySidIn(empresasConCatalogo);
		paginadoResponse.setElementos(empresas);
		return paginadoResponse;
	}

	public PaginadoResponse<List<Company>> getEmpresasWithVisibility(PaginadoRequest paginado, String idEmpresa)
			throws ModelException {
		/*
		TypedQuery<Empresa> query = (TypedQuery<Empresa>) em.createNativeQuery(Empresa.query_empresasConVisibilidad, Empresa.class);//nativeTODO
		query.setParameter("empresa", idEmpresa);
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNativeQuery(Empresa.query_contarEmpresasConVisibilidad);
		queryContar.setParameter("empresa", idEmpresa);
		return Utils.paginar(paginado, query, queryContar);*/

		PaginadoResponse<List<Company>> paginadoResponse = new PaginadoResponse<List<Company>>();
		List<Company> empresas = empresaRepository.findAllByIdNot(idEmpresa);
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(idEmpresa).andOperator(Criteria.where("esPublico").is(true), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if(productos.size() > 0){
			paginadoResponse.setElementos(empresas);
			paginadoResponse.setTotal((long)empresas.size());
		}else{
			List<Company> empresasConVisibilidad = new ArrayList<>();
			for (Company empresa: empresas) {
				Query queryProductoVisible = new Query();
				query.addCriteria(Criteria.where("sempresa").is(idEmpresa).orOperator(Criteria.where("sempresasConVisibilidad").in(empresa.getId()), Criteria.where("sgruposConVisibilidad").in(empresa.getSempresaGrupos())));
				productos = mongoOperations.find(queryProductoVisible, Producto.class);
				if(productos.size() > 0){
					empresasConVisibilidad.add(empresa);
				}
			}
			paginadoResponse.setElementos(empresasConVisibilidad);
			paginadoResponse.setTotal((long) empresasConVisibilidad.size());
		}
		return paginadoResponse;
	}

	public PaginadoResponse<List<Company>> getProveedoresEmpresa(PaginadoRequest paginado, String idEmpresa)
			throws ModelException {
		Company empresa = empresaRepository.findById(idEmpresa).get();
		List<Company> proveedores = new ArrayList<>();

		Aggregation listaDeVentasAggregation = Aggregation.newAggregation(match(Criteria.where("eliminado").is(false).orOperator(Criteria.where("sempresas").in(idEmpresa), Criteria.where("sgrupos").in(empresa.getSempresaGrupos()))), group("sempresa"));
		List<ListaDeVenta> listaDeVentas = mongoTemplate.aggregate(listaDeVentasAggregation, "ListaDeVenta", ListaDeVenta.class).getMappedResults();

		for (ListaDeVenta listaDeVenta: listaDeVentas) {
			Company proveedor = empresaRepository.findById(listaDeVenta.getId()).get();
			proveedores.add(proveedor);
		}
		PaginadoResponse<List<Company>> paginadoResponse = new PaginadoResponse<List<Company>>();
		paginadoResponse.setElementos(proveedores);
		paginadoResponse.setTotal((long) proveedores.size());
		return paginadoResponse;
	}

	public PaginadoResponse<List<Company>> getEmpresasConProductosVisibles(PaginadoRequest paginado, String idEmpresa)
			throws ModelException {
		/*String sqlProveedores = paginado.getUriInfo().map(e -> e.getQueryParameters()).map(e -> {
			String sql = Utils.constructorDinamicoFiltros(Empresa.query_empresasConProductosVisibles, e);
			sql = Utils.constructorDinamicoOrdenamientos(sql, e);
			return sql;
		}).orElse(Empresa.query_empresasConProductosVisibles);
		TypedQuery<Empresa> query = (TypedQuery<Empresa>) em.createNativeQuery(sqlProveedores, Empresa.class);
		query.setParameter("empresa", idEmpresa);

		String sqlContarProveedores = paginado.getUriInfo().map(e -> e.getQueryParameters())
				.map(e -> Utils.constructorDinamicoFiltros(Empresa.query_contarEmpresasConProductosVisibles, e))
				.orElse(Empresa.query_contarEmpresasConProductosVisibles);
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNativeQuery(sqlContarProveedores);
		queryContar.setParameter("empresa", idEmpresa);
		return Utils.paginar(paginado, query, queryContar);*/
		PaginadoResponse<List<Company>> paginadoResponse = new PaginadoResponse<List<Company>>();
		return paginadoResponse;
	}

	/**
	 * 
	 * Devuelve un {@link List}<{@link Company}> con las que tiene un
	 * {@link UsuarioEmpresa} el {@link Usuario} con Id pasado por parámetros
	 * 
	 * @param idUsuario
	 * @return
	 */
	public List<Company> obtenerEmpresasAsociadasUsuario(String idUsuario) {
		/*TypedQuery<Empresa> query = em.createNamedQuery("obtenerEmpresasAsociadasUsuario", Empresa.class);
		query.setParameter("usuario_id", idUsuario);
		List<Empresa> listaEmpresas = query.getResultList();*/
		List<Company> listaEmpresas = new ArrayList<>();
		return listaEmpresas;
	}

	public Set<Company> obtenerEmpresasAsociadasUsuario(Usuario usuario) {
		Set<Company> listaEmpresas = new HashSet<>();
		for (UsuarioEmpresa usuarioEmpresa : usuario.getUsuariosEmpresas()) {
			listaEmpresas.add(usuarioEmpresa.getEmpresa());
		}
		return listaEmpresas;
	}

}