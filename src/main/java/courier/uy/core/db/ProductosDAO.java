package courier.uy.core.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.core.MultivaluedMap;

import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.resources.dto.UsuarioJwt;
import courier.uy.core.entity.*;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.repository.IProductoRepository;
import courier.uy.core.repository.IUserRepository;
import courier.uy.core.resources.dto.ExcelProduct;
import courier.uy.core.resources.dto.PaginadoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Component
public class ProductosDAO{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductosDAO.class);

	@Autowired
    IProductoRepository productoRepository;

	@Autowired
    IEmpresaRepository empresaRepository;

	@Autowired
    IUserRepository userRepository;

	private final MongoTemplate mongoTemplate;

	private final MongoOperations mongoOperations;

	public ProductosDAO(MongoTemplate mongoTemplate, MongoOperations mongoOperations) {
		this.mongoTemplate = mongoTemplate;
		this.mongoOperations = mongoOperations;
	}

	public PaginadoResponse<List<Producto>> getAll(PaginadoRequest paginado, Company empresa) throws ModelException {
		List<Producto> productos = productoRepository.findAllBySempresa(empresa.getSId());
		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<>();
		paginadoResponse.setElementos(productos);
		paginadoResponse.setTotal((long) productos.size());
		return paginadoResponse;
	}

	@Transactional
	public Set<Producto> getAll(Company empresa) {
		Optional<Company> emp = empresaRepository.findById(empresa.getId());
		Set<Producto> productos = emp.get().getProductosEmpresa();
		productos.removeIf(p -> p.getEliminado() == true);
		return productos;
	}

	public List<Producto> getAllByEmpresa(Company empresa) {
		List<Producto> productos = productoRepository.findAllBySempresa(empresa.getSId());
		return productos;
	}

	public Producto findById(String id) {
		Optional<Producto> producto = productoRepository.findById(id);
		return producto.isPresent() ? producto.get() : null;
	}

	public Producto save(Producto producto, UsuarioJwt user) {
		Usuario usuario = userRepository.findOneById(user.getId());
		Company empresa = usuario.getUsuariosEmpresas().iterator().next().getEmpresa();
		producto.setEmpresa(empresa);
		producto.setSempresa(empresa.getSId());
		producto = productoRepository.save(producto);
		producto.setSId(producto.getId());
		return productoRepository.save(producto);
	}

	public Boolean insertBatch(List<ExcelProduct> excelProducts) {
		try {
			for (ExcelProduct excelProduct : excelProducts) {
				if (excelProduct.getWasCreated()) {
					Producto toPersist = excelProduct.getProduct();
					toPersist = productoRepository.save(toPersist);
					toPersist.setSId(toPersist.getId());
					productoRepository.save(toPersist);
				}
			}
		} catch (Exception e) {
			LOGGER.warn("CATCH MESSAGE: " + e.getMessage());
		}

		return true;
	}

	public Optional<Producto> findByCpp(String cpp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("cpp").is(cpp).andOperator(Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));
		return Optional.ofNullable(null);
	}

	public Optional<Producto> findByIdEmpresaAndCpp(String empresaId, String cpp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresaId).andOperator(Criteria.where("cpp").is(cpp), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));
		return Optional.ofNullable(null);
	}

	public Optional<Producto> findByGtinAndCppAndEmpresaForExcelUpdate(String gtin, String cpp, String empresaId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresaId).andOperator(Criteria.where("gtin").is(gtin), Criteria.where("cpp").is(cpp)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));
		return Optional.ofNullable(null);
	}


	public Producto save(Producto p) {
		p.setFechaCreacion();
		p.setFechaEdicion();
		p = productoRepository.save(p);
		p.setSId(p.getId());
		return productoRepository.save(p);
	}

	public Producto update(Producto p) {
		p.setFechaEdicion();
		return productoRepository.save(p);
	}

	/**
	 * 
	 * Devuelve el {@link Producto} con el GTIN pasado por parámetros sin importar
	 * la {@link Company} a la que pertenezca. Se tiene en cuenta que no esté
	 * eliminado.
	 * 
	 * @param gtin
	 * @return
	 */
	public Optional<Producto> findByGtin(String gtin) {
		return this.findByGtin(gtin, null);
	}

	/**
	 * 
	 * * Devuelve el {@link Producto} con el GTIN pasado verificando que pertenezca
	 * a la {@link Company} pasada por parámetros. Se tiene en cuenta que no esté
	 * eliminado.
	 * 
	 * @param gtin
	 * @param "empresaId"
	 * @return
	 */
	public Optional<Producto> findByGtin(String gtin, String empresaId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresaId).andOperator(Criteria.where("gtin").is(gtin), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));
		return Optional.ofNullable(null);
	}

	public PaginadoResponse<List<Producto>> getAll(PaginadoRequest paginado) throws ModelException {
		List<Producto> productos = productoRepository.findAll();
		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<>();
		paginadoResponse.setElementos(productos);
		paginadoResponse.setTotal((long) productos.size());
		return paginadoResponse;
	}

	public void deleteAll(String idEmpresa) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaUpdate<Producto> criteria = builder.createCriteriaUpdate(Producto.class);
		Root<Producto> i = criteria.from(Producto.class);
		Predicate criterioSeleccion = builder.and(Utils.predicadoEliminado(builder, i),
				builder.equal(i.get("empresa"), idEmpresa));
		criteria.where(criterioSeleccion);
		criteria.set("eliminado", true);
		em.createQuery(criteria).executeUpdate();*/
	}

	public List<Producto> getAll() {
		List<Producto> listaProductos = productoRepository.findAll();
		return listaProductos;
	}

	public Optional<Producto> findByGtinAndCpp(String gtin, String cpp) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gtin").is(gtin).andOperator(Criteria.where("cpp").is(cpp), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));

		return Optional.ofNullable(null);
	}

	public Optional<Producto> findByGtinAndCppAndEmpresa(String gtin, String cpp, String empresaId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("gtin").is(gtin).andOperator(Criteria.where("cpp").is(cpp), Criteria.where("sempresa").is(empresaId), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		if (productos.size() != 0)
			return Optional.of(productos.get(0));
		return Optional.ofNullable(null);
	}

	public List<Producto> findByKey(String key, String value) {
		Query query = new Query();
		query.addCriteria(Criteria.where("key").is(value).andOperator(Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return productos;
	}

	public Optional<Set<Producto>> findByParameter(MultivaluedMap<String, String> params) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Producto> criteria = builder.createQuery(Producto.class);
		Root<Producto> i = criteria.from(Producto.class);

		Iterator<String> it = params.keySet().iterator();

		try {
			while (it.hasNext()) {
				String key = (String) it.next();

				List<String> lst = new ArrayList<String>();
				lst.add("ABC");

				criteria.select(i).where(
						builder.like(i.get(key), MatchMode.ANYWHERE.toMatchString(params.getFirst(key))),
						builder.in(i.get("id").in(lst)));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Malos parámetros.");
		}

		TypedQuery<Producto> query = em.createQuery(criteria);
		List<Producto> listaProductos = query.getResultList();
		Set<Producto> toReturn = new HashSet<Producto>();
		toReturn.addAll(listaProductos);
		return Optional.of(toReturn);*/
		return Optional.of(null);
	}

	@SuppressWarnings("unused")
	private Categoria buscarCategoria(Categoria categoria, Company empresa) {
		Query query = new Query();
		query.addCriteria(Criteria.where("nombre").is(categoria.getNombre()).andOperator(Criteria.where("sempresa").is(empresa.getSId()), Criteria.where("eliminado").is(false)));
		try {
			Categoria cat = mongoOperations.findOne(query, Categoria.class);
			return cat;
		} catch (NoResultException e) {
			categoria.setNivel((long) 1);
		}
		return categoria;
	}

	public List<Producto> getAllPublic() {
		Query query = new Query();
		query.addCriteria(Criteria.where("esPublico").is(true).andOperator(Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return productos;
	}

	public PaginadoResponse<List<Producto>> getAllPublicFromBusiness(PaginadoRequest paginado, Company empresa)
			throws ModelException {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresa.getSId()).andOperator(Criteria.where("esPublico").is(true), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<>();
		paginadoResponse.setElementos(productos);
		paginadoResponse.setTotal((long) productos.size());
		return paginadoResponse;
	}

	public List<Producto> getAllPublicFromBusiness(Company provider) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(provider.getSId()).andOperator(Criteria.where("esPublico").is(true), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return productos;
	}

	public PaginadoResponse<List<Producto>> getAllPrivateFromBusiness(PaginadoRequest paginado, Company empresa)
			throws ModelException {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresa.getSId()).andOperator(Criteria.where("esPrivado").is(true), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<>();
		return paginadoResponse;
	}

	public List<Producto> getAllPrivateFromBusiness(Company provider) {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(provider.getSId()).andOperator(Criteria.where("esPrivado").is(true), Criteria.where("eliminado").is(false)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<>();
		return productos;
	}

	public Set<Producto> getVisibleByBussinesOnSaleList(Company target, ListaDeVenta lv) {
		/*String where = "";
	    String getVisibleByBussinesOnSaleList = "select distinct * from productos AS p join productos_listas_de_venta as plv on plv.producto_id = p.id and plv.lista_de_venta_id = :lv and p.empresa_id = :provider where ((plv.es_publico = 1) OR EXISTS( SELECT * FROM lista_de_venta_visibilidad_empresa AS lvve WHERE lvve.producto_id = p.id AND lvve.empresa_id = :target LIMIT 1) OR EXISTS ( SELECT * FROM lista_de_venta_visibilidad_grupo AS lvvg WHERE lvvg.producto_id = p.id AND EXISTS( SELECT * FROM grupos_empresas AS ge WHERE ge.empresa_id = :target AND ge.grupo_id = lvvg.grupo_id LIMIT 1)))";
		where = " And ((p.suspendido_desde is not null AND p.suspendido_hasta is not null) OR (p.suspendido_desde is null AND p.suspendido_hasta is null) OR(p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25)))";
	    TypedQuery<Producto> query = (TypedQuery<Producto>) em.createNativeQuery(getVisibleByBussinesOnSaleList + where,
				Producto.class);
		query.setParameter("target", target.getId());
		query.setParameter("lv", lv.getId());
		query.setParameter("provider", lv.getEmpresa().getId());
		List<Producto> listaProductos = query.getResultList();

		Set<Producto> toReturn = new HashSet<Producto>();
		toReturn.addAll(listaProductos);*/

		Set<Producto> toReturn = new HashSet<Producto>();
		return toReturn;
	}

	public List<String> getVisibleByBussinesOnSaleListPedido(Company target, ListaDeVenta lv, PaginadoRequest paginado) {
		/*String where = getWhere(paginado);
		String getVisibleByBussinesOnSaleList = "select padre.nombre from productos AS p join productos_listas_de_venta as plv on plv.producto_id = p.id and plv.lista_de_venta_id = :lv and p.empresa_id = :provider join categorias as c on p.categoria = c.id join categorias as padre on c.padre = padre.id where ((plv.es_publico = 1) OR EXISTS( SELECT * FROM lista_de_venta_visibilidad_empresa AS lvve WHERE lvve.producto_id = p.id AND lvve.empresa_id = :target LIMIT 1) OR EXISTS ( SELECT * FROM lista_de_venta_visibilidad_grupo AS lvvg WHERE lvvg.producto_id = p.id AND EXISTS( SELECT * FROM grupos_empresas AS ge WHERE ge.empresa_id = :target AND ge.grupo_id = lvvg.grupo_id LIMIT 1)))";
		String filteredNativeQueryPedidos = this.getFilteredNativeQueryPedidos(paginado, "");
		String SQLQuery = getVisibleByBussinesOnSaleList + where + filteredNativeQueryPedidos;
		Query query= (TypedQuery<Producto>) em.createNativeQuery(SQLQuery);
		query.setParameter("target", target.getId());
		query.setParameter("lv", lv.getId());
		query.setParameter("provider", lv.getEmpresa().getId());
		List<String> divisiones = query.getResultList();*/
		/*
		LookupOperation lookupOperation = LookupOperation.newLookup()
				.from("Producto")
				.localField("sproducto")
				.foreignField("sid")
				.as("producto");

		Aggregation aggregationX = Aggregation.newAggregation(Aggregation.match(Criteria.where("sid").is("5f1202a82c9cc52ed679cbe5")));
		List<ListaDeVentaVisibilidad> resultsd = mongoTemplate.aggregate(aggregationX, "ListaDeVentaVisibilidad", ListaDeVentaVisibilidad.class).getMappedResults();

		Empresa empresa = ue.getEmpresa();

		//db.getCollection('Producto').find({'$expr': {'$and' : [{'$eq': [ {'$ifNull': ['$suspendidoDesde', null]}, null]} ]}, sempresasConVisibilidad: "5f1202352c9cc52ed6798bf4"})
		//db.getCollection('Producto').find({'$expr':{'$gte':[{'$subtract':['$fechaEdicion','$fechaCreacion']},1296000000]}})
		query = new BasicQuery("{'$expr':{'$gte':[{'$subtract':['$fechaEdicion','$fechaCreacion']},1296000000]}}");

		if (paginado.getDiscontinuados() || paginado.getSuspendidos()) {
			if (paginado.getDiscontinuados() && paginado.getSuspendidos()) {
				//where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) < 0)) OR (p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25)))";
			} else if (paginado.getDiscontinuados()) {
				//where = "(p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25))";
			} else {
				//where = "(p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) < 0))";
			}
		} else if (paginado.getOcultarDiscontinuados() && paginado.getOcultarSuspendidos()) {
			//where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) >= 0)) OR (p.suspendido_desde is null AND p.suspendido_hasta is null))";
		} else if (paginado.getOcultarDiscontinuados()) {
			//where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null) OR (p.suspendido_desde is null AND p.suspendido_hasta is null))";
		} else if (paginado.getOcultarSuspendidos()) {
			//where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) >= 0)) OR (p.suspendido_desde is null AND p.suspendido_hasta is null))";
		} else {
			//where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null) OR (p.suspendido_desde is null AND p.suspendido_hasta is null) OR(p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25)))";
		}

		query.addCriteria(Criteria.where("sempresa").is(idProveedor).andOperator(Criteria.where("sempresasConVisibilidad").in(empresa.getSId())));
		List<Producto> userList2 = mongoOperations.find(query, Producto.class);

		if(orExpression.size() > 0 && andExpression.size() > 0){
			query.addCriteria(orCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])).orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}else if(orExpression.size() > 0) {
			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}else if(andExpression.size() > 0) {
			query.addCriteria(orCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		}

		countQuery = query;
		query.limit(25);
		query.skip(0);
		List<Producto> userList = mongoOperations.find(query, Producto.class);
		long total = mongoOperations.count(countQuery, Producto.class);

		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("empresa").is("")));
		List<Producto> results5 = mongoTemplate.aggregate(aggregation, "Producto", Producto.class).getMappedResults();

		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<List<Producto>>();
		return paginadoResponse;
		*/
		List<String> divisiones = new ArrayList<>();
		return divisiones;
	}

	public List<String> getMarcasVisibleByBussinesOnSaleListPedido(Company target, ListaDeVenta lv, PaginadoRequest paginado) {
		/*String where = getWhere(paginado);
		String getVisibleByBussinesOnSaleList = "select p.marca from productos AS p join productos_listas_de_venta as plv on plv.producto_id = p.id and plv.lista_de_venta_id = :lv and p.empresa_id = :provider join categorias as c on p.categoria = c.id join categorias as padre on c.padre = padre.id where ((plv.es_publico = 1) OR EXISTS( SELECT * FROM lista_de_venta_visibilidad_empresa AS lvve WHERE lvve.producto_id = p.id AND lvve.empresa_id = :target LIMIT 1) OR EXISTS ( SELECT * FROM lista_de_venta_visibilidad_grupo AS lvvg WHERE lvvg.producto_id = p.id AND EXISTS( SELECT * FROM grupos_empresas AS ge WHERE ge.empresa_id = :target AND ge.grupo_id = lvvg.grupo_id LIMIT 1)))";
		String filteredNativeQueryPedidos = this.getFilteredNativeQueryPedidos(paginado, "");
		String SQLQuery = getVisibleByBussinesOnSaleList + where + filteredNativeQueryPedidos;
		Query query= (TypedQuery<Producto>) em.createNativeQuery(SQLQuery);
		query.setParameter("target", target.getId());
		query.setParameter("lv", lv.getId());
		query.setParameter("provider", lv.getEmpresa().getId());
		List<String> divisiones = query.getResultList();*/
		List<String> divisiones = new ArrayList<>();
		return divisiones;
	}

	public List<String> getLineasVisibleByBussinesOnSaleListPedido(Company target, ListaDeVenta lv, PaginadoRequest paginado) {
		/*String where = getWhere(paginado);
		String getVisibleByBussinesOnSaleList = "select c.nombre from productos AS p join productos_listas_de_venta as plv on plv.producto_id = p.id and plv.lista_de_venta_id = :lv and p.empresa_id = :provider join categorias as c on p.categoria = c.id join categorias as padre on c.padre = padre.id where ((plv.es_publico = 1) OR EXISTS( SELECT * FROM lista_de_venta_visibilidad_empresa AS lvve WHERE lvve.producto_id = p.id AND lvve.empresa_id = :target LIMIT 1) OR EXISTS ( SELECT * FROM lista_de_venta_visibilidad_grupo AS lvvg WHERE lvvg.producto_id = p.id AND EXISTS( SELECT * FROM grupos_empresas AS ge WHERE ge.empresa_id = :target AND ge.grupo_id = lvvg.grupo_id LIMIT 1)))";
		String filteredNativeQueryPedidos = this.getFilteredNativeQueryPedidos(paginado, "");
		String SQLQuery = getVisibleByBussinesOnSaleList + where + filteredNativeQueryPedidos;
		Query query= (TypedQuery<Producto>) em.createNativeQuery(SQLQuery);
		query.setParameter("target", target.getId());
		query.setParameter("lv", lv.getId());
		query.setParameter("provider", lv.getEmpresa().getId());
		List<String> divisiones = query.getResultList();*/
		List<String> divisiones = new ArrayList<>();
		return divisiones;
	}

	public Producto getProductByBussisnesRutAndCpp(Long rut, Long cpp) {
		/*TypedQuery<Producto> query = em.createNamedQuery("getPorductByBussisnesRutAndCpp",
				Producto.class);
		query.setParameter("rut", rut);
		query.setParameter("cpp", cpp);
		List<Producto> productos = query.getResultList();
		Producto producto = null;
		if (!productos.isEmpty()) {
			producto = productos.get(0);
		}*/

		Producto producto = new Producto();
		return producto;
	}

	public List<Producto> getVisibleByBussinesOnSaleListPedido(PaginadoRequest paginado, Company target,
			ListaDeVenta lv) throws ModelException {

		/*String orderBy = "";
		if(paginado.getOrderBy().equals("marca")){
			orderBy = " ORDER BY p.marca ASC ";
		}else if(paginado.getOrderBy().equals("linea")){
			orderBy = " ORDER BY padre.nombre ASC ";
		}

		String where = getWhere(paginado);

	    String getVisiblesDivision = "select distinct * from productos AS p join productos_listas_de_venta as plv on plv.producto_id = p.id and plv.lista_de_venta_id = :lv and p.empresa_id = :provider join categorias as c on p.categoria = c.id join categorias as padre on c.padre = padre.id where padre.nombre=:division AND ((plv.es_publico = 1) OR EXISTS( SELECT * FROM lista_de_venta_visibilidad_empresa AS lvve WHERE lvve.producto_id = p.id AND lvve.empresa_id = :target LIMIT 1) OR EXISTS ( SELECT * FROM lista_de_venta_visibilidad_grupo AS lvvg WHERE lvvg.producto_id = p.id AND EXISTS( SELECT * FROM grupos_empresas AS ge WHERE ge.empresa_id = :target AND ge.grupo_id = lvvg.grupo_id LIMIT 1)))";
		String filteredNativeQueryPedidos = this.getFilteredNativeQueryPedidos(paginado, "");
		String SQLQuery = getVisiblesDivision + where + filteredNativeQueryPedidos + orderBy + "LIMIT :limit OFFSET :offset";

        TypedQuery<Producto> query = (TypedQuery<Producto>) em.createNativeQuery(SQLQuery, Producto.class);
		final String targetId = target.getId();
		int offset = (int) ((paginado.getPagina() - 1) * paginado.getCantidad());
		query.setParameter("target", targetId);
		final String lvId = lv.getId();
		query.setParameter("lv", lvId);
		final String provId = lv.getEmpresa().getId();
		query.setParameter("provider", provId);
        query.setParameter("division", paginado.getDivision());
		query.setParameter("limit", paginado.getCantidad());
		query.setParameter("offset", offset);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public String getWhere(PaginadoRequest paginado){
		String where = "";
		if (paginado.getDiscontinuados() || paginado.getSuspendidos()) {
			if (paginado.getDiscontinuados() && paginado.getSuspendidos()) {
				where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) < 0)) OR (p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25)))";
			} else if (paginado.getDiscontinuados()) {
				where = "(p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25))";
			} else {
				where = "(p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) < 0))";
			}
		} else if (paginado.getOcultarDiscontinuados() && paginado.getOcultarSuspendidos()) {
			where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) >= 0)) OR (p.suspendido_desde is null AND p.suspendido_hasta is null))";
		} else if (paginado.getOcultarDiscontinuados()) {
			where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null) OR (p.suspendido_desde is null AND p.suspendido_hasta is null))";
		} else if (paginado.getOcultarSuspendidos()) {
			where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null AND (select DATEDIFF(NOW(), p.suspendido_hasta) >= 0)) OR (p.suspendido_desde is null AND p.suspendido_hasta is null))";
		} else {
			where = "((p.suspendido_desde is not null AND p.suspendido_hasta is not null) OR (p.suspendido_desde is null AND p.suspendido_hasta is null) OR(p.suspendido_desde is not null AND p.suspendido_hasta is null AND (select DATEDIFF(NOW(), suspendido_desde) < 25)))";
		}

		return where.equals("") ? where: " AND " + where;
	}
	
	public PaginadoResponse<List<Producto>> getVisibleByBussines(PaginadoRequest paginado, String idProveedor,
			UsuarioEmpresa ue) throws ModelException {
		List<String> paginadoRequestFilters = paginado.getFilters();
		List<String> paginadoRequestFiltersMarcas = paginado.getMarcas();
		List<String> paginadoRequestFiltersLineas = paginado.getLineas();
		List<String> paginadoRequestFiltersDivisiones = paginado.getDivisiones();

		Query query = new Query();
		Criteria orCriteria = new Criteria();
		List<Criteria> orExpression =  new ArrayList<>();
		List<Criteria> andExpression =  new ArrayList<>();

		Company empresa = ue.getEmpresa();

		for (String filter : paginadoRequestFilters) {
			Criteria expression = new Criteria();
			expression.orOperator(Criteria.where("descripcion").regex(filter, "i"), Criteria.where("cpp").regex(filter, "i"));
			andExpression.add(expression);
		}

		for (String marca : paginadoRequestFiltersMarcas) {
			Criteria expression = new Criteria();
			expression.and("marca").regex(marca, "i");
			orExpression.add(expression);
		}

		for (String linea : paginadoRequestFiltersLineas) {
			Criteria expression = new Criteria();
			expression.and("linea").regex(linea, "i");
			orExpression.add(expression);
		}

		for (String division : paginadoRequestFiltersDivisiones) {
			Criteria expression = new Criteria();
			expression.and("division").regex(division, "i");
			orExpression.add(expression);
		}

		query.addCriteria(Criteria.where("sempresa").is(idProveedor).andOperator(Criteria.where("eliminado").is(false)).orOperator(Criteria.where("esPublico").is(true), Criteria.where("sempresasConVisibilidad").in(empresa.getId()), Criteria.where("sgruposConVisibilidad").in(empresa.getSempresaGrupos())));

		if(orExpression.size() > 0 && andExpression.size() > 0){
			query.addCriteria(orCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])).orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}else if(orExpression.size() > 0) {
			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}else if(andExpression.size() > 0) {
			query.addCriteria(orCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		}

		long total = mongoOperations.count(query, Producto.class);

		query.limit(paginado.getCantidad().intValue());
		query.skip(paginado.getPagina() - 1);
		List<Producto> productos = mongoOperations.find(query, Producto.class);

		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<List<Producto>>();
		paginadoResponse.setElementos(productos);
		paginadoResponse.setTotal(total);
		return paginadoResponse;
	}

	public PaginadoResponse<List<Producto>> getMyVisibleProductForSelectedBussines(PaginadoRequest paginado,
			String idProveedor, UsuarioEmpresa ue) throws ModelException {
		/*TypedQuery<Producto> query = em.createNamedQuery("obtenerProductosVisiblesPorPorveedor",
				Producto.class);
		query.setParameter("proveedor", ue.getEmpresa().getId());
		query.setParameter("empresa", idProveedor);
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNamedQuery("countVisibleByBussines");
		queryContar.setParameter("proveedor", ue.getEmpresa().getId());
		queryContar.setParameter("empresa", idProveedor);

		return Utils.paginar(paginado, query, queryContar);*/
		PaginadoResponse<List<Producto>> paginadoResponse =  new PaginadoResponse<List<Producto>>();
		return paginadoResponse;
	}

	public PaginadoResponse<List<Producto>> obtenerProductosEmpresa(PaginadoRequest paginado, String idProveedor,
			UsuarioEmpresa ue) throws ModelException {
		List<String> paginadoRequestFilters = paginado.getFilters();
		List<String> paginadoRequestFiltersMarcas = paginado.getMarcas();
		List<String> paginadoRequestFiltersLineas = paginado.getLineas();
		List<String> paginadoRequestFiltersDivisiones = paginado.getDivisiones();

		Query query = new Query();
		Criteria orCriteria = new Criteria();
		List<Criteria> orExpression =  new ArrayList<>();
		List<Criteria> andExpression =  new ArrayList<>();

		for (String filter : paginadoRequestFilters) {
			Criteria expression = new Criteria();
			expression.orOperator(Criteria.where("descripcion").regex(filter, "i"), Criteria.where("cpp").regex(filter, "i"));
			andExpression.add(expression);
		}

		for (String marca : paginadoRequestFiltersMarcas) {
			Criteria expression = new Criteria();
			expression.and("marca").regex(marca, "i");
			orExpression.add(expression);
		}

		for (String linea : paginadoRequestFiltersLineas) {
			Criteria expression = new Criteria();
			expression.and("linea").regex(linea, "i");
			orExpression.add(expression);
		}

		for (String division : paginadoRequestFiltersDivisiones) {
			Criteria expression = new Criteria();
			expression.and("division").regex(division, "i");
			orExpression.add(expression);
		}

		query.addCriteria(Criteria.where("sempresa").is(ue.getEmpresa().getSId()).andOperator(Criteria.where("eliminado").is(false)));

		if(orExpression.size() > 0 && andExpression.size() > 0){
			query.addCriteria(orCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])).orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}else if(orExpression.size() > 0) {
			query.addCriteria(orCriteria.orOperator(orExpression.toArray(new Criteria[orExpression.size()])));
		}else if(andExpression.size() > 0) {
			query.addCriteria(orCriteria.andOperator(andExpression.toArray(new Criteria[andExpression.size()])));
		}

		long total = mongoOperations.count(query, Producto.class);

		query.limit(paginado.getCantidad().intValue());
		query.skip(paginado.getPagina() - 1);
		List<Producto> productos = mongoOperations.find(query, Producto.class);


		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<List<Producto>>();
		paginadoResponse.setElementos(productos);
		paginadoResponse.setTotal(total);
		return paginadoResponse;
	}

	public List<Producto> obtenerProductosEmpresaParaExportar(PaginadoRequest paginado, Company empresa)
			throws ModelException {
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresa.getSId()));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return productos;
	}

	public List<Producto> obtenerProductosEmpresaMarcas(PaginadoRequest paginado, String idProveedor, Company empresa)
			throws ModelException {
		/*TypedQuery<Producto> query = em.createQuery(Producto.queryObtenerProductosEmpresasMarcas,
				Producto.class);
		query.setParameter("empresa", empresa);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public List<Producto> obtenerProductosEmpresaDivisiones(PaginadoRequest paginado, String idProveedor, Company empresa)
			throws ModelException {
		/*TypedQuery<Producto> query = em.createQuery(Producto.queryObtenerProductosEmpresasDivisiones,
				Producto.class);
		query.setParameter("empresa", empresa);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public List<Producto> obtenerProductosEmpresaLineas(PaginadoRequest paginado, String idProveedor, Company empresa)
			throws ModelException {
		/*TypedQuery<Producto> query = em.createQuery(Producto.queryObtenerProductosEmpresasLineas,
				Producto.class);
		query.setParameter("empresa", empresa);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public List<Producto> obtenerProductosEmpresaProveedorMarcas(PaginadoRequest paginado, String idProveedor,
			Company empresa) throws ModelException {
		/*TypedQuery<Producto> query = em.createNamedQuery("obtenerProductosVisiblesPorPorveedorFiltroMarcas", Producto.class);
		query.setParameter("empresa", empresa.getId());
		query.setParameter("proveedor", idProveedor);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public List<Producto> obtenerProductosEmpresaProveedorDivisiones(PaginadoRequest paginado, String idProveedor,
			Company empresa) throws ModelException {
		/*TypedQuery<Producto> query = em.createNamedQuery("obtenerProductosVisiblesPorPorveedorFiltroDivisiones", Producto.class);
		query.setParameter("empresa", empresa.getId());
		query.setParameter("proveedor", idProveedor);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public List<Producto> obtenerProductosEmpresaProveedorLineas(PaginadoRequest paginado, String idProveedor,
			Company empresa) throws ModelException {
		/*TypedQuery<Producto> query = em.createNamedQuery("obtenerProductosVisiblesPorPorveedorFiltroLineas", Producto.class);
		query.setParameter("empresa", empresa.getId());
		query.setParameter("proveedor", idProveedor);
		List<Producto> productos = query.getResultList();*/
		List<Producto> productos = new ArrayList<>();
		return productos;
	}

	public List<Set<String>> obtenerProductosVisiblesPorPorveedorFiltros(PaginadoRequest paginado, String idProveedor,
																 Company empresa) throws ModelException {
		Set<String> marcas = new HashSet<>();
		Set<String> lineas = new HashSet<>();
		Set<String> divisiones = new HashSet<>();

		Aggregation filtrosMarcaAggregation = Aggregation.newAggregation(match(Criteria.where("sempresa").is(empresa.getSId()).andOperator(Criteria.where("eliminado").is(false))), group("marca"));
		List<Producto> productosAgrupadosPorMarca = mongoTemplate.aggregate(filtrosMarcaAggregation, "Producto", Producto.class).getMappedResults();

		Aggregation filtrosLineaAggregation = Aggregation.newAggregation(match(Criteria.where("sempresa").is(empresa.getSId()).andOperator(Criteria.where("eliminado").is(false))), group("linea"));
		List<Producto> productosAgrupadosPorLinea = mongoTemplate.aggregate(filtrosLineaAggregation, "Producto", Producto.class).getMappedResults();

		Aggregation filtrosDivisionAggregation = Aggregation.newAggregation(match(Criteria.where("sempresa").is(empresa.getSId()).andOperator(Criteria.where("eliminado").is(false))), group("division"));
		List<Producto> productosAgrupadosPorDivision = mongoTemplate.aggregate(filtrosDivisionAggregation, "Producto", Producto.class).getMappedResults();

		for (Producto producto: productosAgrupadosPorMarca) {
			if(producto.getId() == null){
				marcas.add("");
			}else {
				marcas.add(producto.getId());
			}
		}
		for (Producto producto: productosAgrupadosPorLinea) {
			if(producto.getId() == null){
				lineas.add("");
			}else {
				lineas.add(producto.getId());
			}
		}
		for (Producto producto: productosAgrupadosPorDivision) {
			if(producto.getId() == null){
				divisiones.add("");
			}else{
				divisiones.add(producto.getId());
			}

		}
		List<Set<String>> filtros = new ArrayList<>();
		filtros.add(0, marcas);
		filtros.add(1, lineas);
		filtros.add(2, divisiones);
		return filtros;
	}

	public List<String> obtenerProductosEmpresaArray(UsuarioEmpresa ue) {
		List<String> stringList = new ArrayList<>();
		stringList.addAll(ue.getEmpresa().getSproductosEmpresa());
		return stringList;
	}

	public PaginadoResponse<List<Producto>> getVisibleForBussines(PaginadoRequest paginado, String idEmpresa,
			UsuarioEmpresa ue) throws ModelException {
		/*TypedQuery<Producto> query = em.createNamedQuery("obtenerProductosVisiblesPorPorveedor",
				Producto.class);
		query.setParameter("empresa", idEmpresa);
		query.setParameter("proveedor", ue.getEmpresa().getId());
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNamedQuery("countVisibleByBussines");
		queryContar.setParameter("empresa", idEmpresa);
		queryContar.setParameter("proveedor", ue.getEmpresa().getId());
		return Utils.paginar(paginado, query, queryContar);*/

		Company empresa = empresaRepository.findById(idEmpresa).get();
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(ue.getEmpresa().getId()).andOperator(Criteria.where("eliminado").is(false)).orOperator(Criteria.where("esPublico").is(true), Criteria.where("sempresasConVisibilidad").in(empresa.getId()), Criteria.where("sgruposConVisibilidad").in(empresa.getSempresaGrupos()) ));
		List<Producto> productos = mongoOperations.find(query, Producto.class);

		PaginadoResponse<List<Producto>> paginadoResponse = new PaginadoResponse<List<Producto>>();
		paginadoResponse.setElementos(productos);
		paginadoResponse.setTotal((long) productos.size());
		return paginadoResponse;
	}

	public PaginadoResponse<List<Grupo>> obtenerGruposConVisibilidadEnProducto(PaginadoRequest paginado,
                                                                               String idProducto, String idEmpresa) throws ModelException {
		/*TypedQuery<Grupo> query = em.createNamedQuery("obtenerGruposConVisibilidadEnProducto",
				Grupo.class);
		query.setParameter("producto", idProducto);
		query.setParameter("empresa", idProducto);
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNamedQuery("contarGruposConVisibilidadEnProducto");
		queryContar.setParameter("producto", idProducto);
		queryContar.setParameter("empresa", idProducto);

		return Utils.paginar(paginado, query, queryContar);*/
		PaginadoResponse<List<Grupo>> paginadoResponse = new PaginadoResponse<List<Grupo>>();
		return paginadoResponse;
	}

	public PaginadoResponse<List<Company>> obtenerEmpresasConVisibilidadEnProducto(PaginadoRequest paginado,
                                                                                   String idProducto, String idEmpresa) throws ModelException {

		/*TypedQuery<Empresa> query = em.createNamedQuery("obtenerEmpresasConVisibilidadEnProducto",
				Empresa.class);
		query.setParameter("producto", idProducto);
		@SuppressWarnings("unchecked")
		TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNamedQuery("contarEmpresasConVisibilidadEnProducto");
		queryContar.setParameter("producto", idProducto);

		return Utils.paginar(paginado, query, queryContar);*/
		PaginadoResponse<List<Company>> paginadoResponse = new PaginadoResponse<List<Company>>();
		return paginadoResponse;
	}

	/**
	 * 
	 * Este método devuelve un {@link Optional}<{@link Producto}> conteniendo el
	 * {@link Producto} en cuestión si está visible para la {@link Company} también
	 * pasada por parámetros.
	 * <p>
	 * Se toma como criterio para determinar la Visibilidad el cumplimiento de las
	 * siguientes condiciones:
	 * <p>
	 * 1. El id del Producto coincide con el pasado por parámetros <br>
	 * 2. El producto está Público o Privado <br>
	 * 3. El producto tiene asignada visibilidad directa a la Empresa pasada por
	 * parámetros o mediante un Grupo
	 * </p>
	 * </p>
	 * 
	 * @param idEmpresa
	 * @param idProducto
	 * @return
	 */
	public Optional<Producto> obtenerProductoVisiblePorEmpresa(String idEmpresa, String idProducto) {
		Company empresa = empresaRepository.findById(idEmpresa).get();
		Set<String> gruposEmpresa = empresa.getSempresaGrupos();
		Query query = new Query();
		query.addCriteria(Criteria.where("sid").is(idProducto).orOperator(Criteria.where("esPublico").is(true), Criteria.where("sempresa").is(idEmpresa), Criteria.where("sempresasConVisibilidad").in(idEmpresa), Criteria.where("sgruposConVisibilidad").in(gruposEmpresa)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		return Optional.ofNullable(productos.isEmpty() ? null : productos.get(0));
	}


	@Transactional
	public void eliminarVisibilidadProducto(Producto producto) {
		/*String queryEliminarVisivilidad = "DELETE FROM grupo_visible_por WHERE producto_id = "
				+ producto.getId().toString() + " ; ";
		em.createNativeQuery(queryEliminarVisivilidad).executeUpdate();
		queryEliminarVisivilidad = " DELETE FROM visible_por WHERE producto_id = " + producto.getId().toString() + "; ";
		em.createNativeQuery(queryEliminarVisivilidad).executeUpdate();*/
	}

	@Transactional
	public void eliminarVisibilidadProductoParaEmpresa(Producto producto, Company empresa) {
		/*String queryEliminarVisivilidad = "DELETE FROM grupo_visible_por WHERE producto_id = "
				+ producto.getId().toString() + " ; ";
		em.createNativeQuery(queryEliminarVisivilidad).executeUpdate();
		queryEliminarVisivilidad = " DELETE FROM visible_por WHERE producto_id = " + producto.getId().toString() + "; ";
		em.createNativeQuery(queryEliminarVisivilidad).executeUpdate();*/
	}

	@Transactional
	public void actualizarEmpresasConVisibilidad(List<Producto> productos, String empresaId) {
		Company empresaAgregarVisibilidad = empresaRepository.findById(empresaId).get();
		for (Producto producto: productos) {
			Producto productoActualizarVisibilidad = productoRepository.findById(producto.getId()).get();
			productoActualizarVisibilidad.getEmpresasConVisibilidad().add(empresaAgregarVisibilidad);
			productoActualizarVisibilidad.getSempresasConVisibilidad().add(empresaAgregarVisibilidad.getId());
			productoRepository.save(productoActualizarVisibilidad);
		}
	}

	@Transactional
	public void actualizarGruposConVisibilidad(List<Producto> productos, String grupoId) {
		/*String query = "INSERT INTO `visible_por` (`empresa_id`, `grupo_id`, `producto_id`, `categoria_id`, `fecha_creacion`, `fecha_edicion`, `eliminado`) VALUES";
		String valuesToInsert = "";
		int index = 0;
		for (Producto producto : productos) {
			if (index + 1 < productos.size()) {
				valuesToInsert += " ( 0, " + grupoId + ", " + producto.getId().toString()
						+ ", 0, CURDATE(), CURDATE(), 0), ";
			} else {
				valuesToInsert += " ( 0, " + grupoId + "," + producto.getId().toString()
						+ ", 0, CURDATE(), CURDATE(), 0)";
			}
			index++;
		}
		em.createNativeQuery(query + valuesToInsert).executeUpdate();*/
	}

	@Transactional
	public void eliminarTodaVisibilidadEmpresasProveedor(String empresaId, String proveedorId) {
		Company empresaParaEliminarDeLaVisibilidad = empresaRepository.findById(empresaId).get();
		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(proveedorId).orOperator(Criteria.where("sempresasConVisibilidad").in(empresaId)));
		List<Producto> productos = mongoOperations.find(query, Producto.class);
		for (Producto producto: productos) {
			producto.getEmpresasConVisibilidad().remove(empresaParaEliminarDeLaVisibilidad);
			producto.getSempresasConVisibilidad().remove(empresaParaEliminarDeLaVisibilidad.getId());
			productoRepository.save(producto);
		}
	}

	public String getFilteredQuery(PaginadoRequest paginado, String productQuery) {
		List<String> paginadoRequestFilters = paginado.getFilters();
		List<String> paginadoRequestFiltersMarcas = paginado.getMarcas();
		List<String> paginadoRequestFiltersLineas = paginado.getLineas();
		List<String> paginadoRequestFiltersDivisiones = paginado.getDivisiones();
		// String productQuery = "FROM Producto p join p.categoria Where p.eliminado = 0
		// AND p.empresa = :empresa";
		String filtros = "";
		List<String> fieldToFilter = new ArrayList<String>();
		fieldToFilter.add("p.cpp ");
		fieldToFilter.add("p.descripcion ");
		fieldToFilter.add("p.marca ");
		fieldToFilter.add("p.categoria.nombre ");
		fieldToFilter.add("p.categoria.padre.nombre ");
		boolean flag = true;
		String filtrosMarcas = "";
		String filtrosLineas = "";
		String filtrosDivisiones = "";
		/*
		 * if (paginadoRequestFilters.size() > 0) { for (int i = 0; i <
		 * fieldToFilter.size(); i++) { for (int j = 0; j <
		 * paginadoRequestFilters.size(); j++) { if (flag) { flag = false; filters +=
		 * fieldToFilter.get(i) + " LIKE '%" + paginadoRequestFilters.get(j) + "%'"; }
		 * else { filters += " OR " + fieldToFilter.get(i) + " LIKE '%" +
		 * paginadoRequestFilters.get(j) + "%'"; } } } }
		 */
		if (paginadoRequestFilters.size() > 0) {

			for (int j = 0; j < paginadoRequestFilters.size(); j++) {
				String filters = "";
				for (int i = 0; i < fieldToFilter.size(); i++) {
					if (i == 0) {
						filters += fieldToFilter.get(i) + " LIKE '%" + paginadoRequestFilters.get(j) + "%'";
					} else {
						filters += " OR " + fieldToFilter.get(i) + " LIKE '%" + paginadoRequestFilters.get(j) + "%'";
					}
				}

				if (j > 0) {
					filtros += "AND (" + filters + ") ";
				} else {
					filtros += "(" + filters + ")";
				}
			}
		}

		if (paginadoRequestFiltersMarcas.size() > 0) {
			flag = true;
			for (int i = 0; i < paginadoRequestFiltersMarcas.size(); i++) {
				if (flag) {
					flag = false;
					filtrosMarcas += "p.marca='" + paginadoRequestFiltersMarcas.get(i) + "'";
				} else {
					filtrosMarcas += " OR " + "p.marca='" + paginadoRequestFiltersMarcas.get(i) + "'";
				}
			}
		}

		if (paginadoRequestFiltersLineas.size() > 0) {
			flag = true;
			for (int i = 0; i < paginadoRequestFiltersLineas.size(); i++) {
				if (flag) {
					flag = false;
					filtrosLineas += "p.categoria.padre.nombre='" + paginadoRequestFiltersLineas.get(i) + "'";
				} else {
					filtrosLineas += " OR " + "p.categoria.padre.nombre='" + paginadoRequestFiltersLineas.get(i) + "'";
				}
			}
		}

		if (paginadoRequestFiltersDivisiones.size() > 0) {
			flag = true;
			for (int i = 0; i < paginadoRequestFiltersDivisiones.size(); i++) {
				if (flag) {
					flag = false;
					filtrosDivisiones += "p.categoria.nombre='" + paginadoRequestFiltersDivisiones.get(i) + "'";
				} else {
					filtrosDivisiones += " OR " + "p.categoria.nombre='" + paginadoRequestFiltersDivisiones.get(i)
							+ "'";
				}
			}
		}

		String sort = paginado.getSort();
		String order = "ASC";
		String querySort = "";
		if (sort.indexOf("-") > -1) {
			order = "DESC";
			sort = sort.substring(sort.indexOf("-") + 1);
		}

		switch (sort) {
			case "cpp":
				querySort = " ORDER BY p.cpp " + order;
				break;
			case "descripcion":
				querySort = " ORDER BY p.descripcion " + order;
				break;
			case "marca":
				querySort = " ORDER BY p.marca " + order;
				break;
			case "linea":
				querySort = " ORDER BY p.categoria.padre.nombre " + order;
				break;
			case "division":
				querySort = " ORDER BY p.categoria.nombre " + order;
				break;
		}

		if (!filtrosLineas.isEmpty()) {
			productQuery += " AND (" + filtrosLineas + ") ";
		}
		if (!filtrosMarcas.isEmpty()) {
			productQuery += " AND (" + filtrosMarcas + ") ";
		}
		if (!filtrosDivisiones.isEmpty()) {
			productQuery += " AND (" + filtrosDivisiones + ") ";
		}
		if (!filtros.isEmpty()) {
			productQuery += " AND (" + filtros + ") ";
		}
		return productQuery + querySort;
	}

}