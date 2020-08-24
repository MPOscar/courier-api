package courier.uy.core.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import courier.uy.core.entity.ProductoAccion;
import courier.uy.core.repository.IProductoAccionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductosAccionesDAO {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductosDAO.class);

	@Autowired
    IProductoAccionRepository productoAccionRepository;

	@PersistenceContext
	private EntityManager em;

	/**
	 * Devuelve el listado de las {@link ProductoAccion} no <b>Acusadas</b>
	 * 
	 * @return {@link List}<{@link ProductoAccion}>
	 */
	public List<ProductoAccion> getAllNotAcknowledged() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ProductoAccion> criteria = builder.createQuery(ProductoAccion.class);
		Root<ProductoAccion> i = criteria.from(ProductoAccion.class);

		criteria.select(i).where(builder.equal(i.get("eliminado"), false), builder.equal(i.get("recibido"), false));

		TypedQuery<ProductoAccion> query = em.createQuery(criteria);
		List<ProductoAccion> listaAcciones = query.getResultList();
		return listaAcciones;
	}

	/**
	 * Devuelve el listado de las {@link ProductoAccion} no <b>Acusadas</b>
	 * correspondientes a la {@link Company} Cliente y a la {@link Company}
	 * Proveedor pasadas por parámetros
	 * 
	 * @param businessId : {@link Company} Cliente
	 * @param providerId : {@link Company} Proveedor
	 * @return {@link List}<{@link ProductoAccion}>
	 */
	public List<ProductoAccion> getAllNotAcknowledgedFromBusinessAndProvider(String businessId, String providerId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ProductoAccion> criteria = builder.createQuery(ProductoAccion.class);
		Root<ProductoAccion> i = criteria.from(ProductoAccion.class);

		criteria.select(i).where(builder.equal(i.get("empresaId"), businessId),
				builder.equal(i.get("proveedorId"), providerId), builder.equal(i.get("eliminado"), false),
				builder.equal(i.get("recibido"), false));

		TypedQuery<ProductoAccion> query = em.createQuery(criteria);
		List<ProductoAccion> listaAcciones = query.getResultList();
		return listaAcciones;
	}

	/**
	 * Devuelve el listado de las {@link ProductoAccion} no <b>Acusadas</b>
	 * correspondientes a la {@link Company} Cliente
	 * 
	 * @param businessId : {@link Company} Cliente
	 * @return {@link List}<{@link ProductoAccion}>
	 */
	public List<ProductoAccion> getAllNotAcknowledgedFromBusiness(String businessId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ProductoAccion> criteria = builder.createQuery(ProductoAccion.class);
		Root<ProductoAccion> i = criteria.from(ProductoAccion.class);

		criteria.select(i).where(builder.equal(i.get("empresaId"), businessId),
				builder.equal(i.get("eliminado"), false), builder.equal(i.get("recibido"), false));

		TypedQuery<ProductoAccion> query = em.createQuery(criteria);
		List<ProductoAccion> listaAcciones = query.getResultList();
		return listaAcciones;
	}

	/**
	 * Devuelve el listado de las {@link ProductoAccion} no <b>Acusadas</b>
	 * correspondientes a la {@link Company} actuando como Proveedor
	 * 
	 * @param providerId : {@link Company} Proveedor
	 * @return {@link List}<{@link ProductoAccion}>
	 */
	public List<ProductoAccion> getAllNotAcknowledgedFromProvider(String providerId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ProductoAccion> criteria = builder.createQuery(ProductoAccion.class);
		Root<ProductoAccion> i = criteria.from(ProductoAccion.class);

		criteria.select(i).where(builder.equal(i.get("proveedorId"), providerId),
				builder.equal(i.get("eliminado"), false), builder.equal(i.get("recibido"), false));

		TypedQuery<ProductoAccion> query = em.createQuery(criteria);
		List<ProductoAccion> listaAcciones = query.getResultList();
		return listaAcciones;
	}

	public ProductoAccion findById(String id) {
		Optional<ProductoAccion> productoAccion = productoAccionRepository.findById(id);
		return productoAccion.isPresent() ? productoAccion.get() : null;
	}

	public ProductoAccion insert(ProductoAccion accion) {
		accion.setFechaCreacion();
		accion.setFechaEdicion();
		return productoAccionRepository.save(accion);
	}

	public ProductoAccion update(ProductoAccion pa) {
		pa.setFechaEdicion();
		return productoAccionRepository.save(pa);
	}

	/**
	 * Devuelve un {@link Optional}<{@link ProductoAccion}> si existe una fila que
	 * coincida con los datos pasados por parámetros <b>Producto-Cliente-Accion</b>
	 * que no esté <b>eliminado</b> ni <b>Acusado</b>
	 * 
	 * @param productId  : {@link Producto} con una {@link ProductoAccion} que debe
	 *                   ser notificada a la {@link Company} Cliente pasada por
	 *                   parámetros
	 * @param businessId : {@link Company} Cliente de la que se quieren encontrar
	 *                   {@link ProductoAccion} pendientes por Acusar
	 * @param action     : Verbo de la {@link ProductoAccion} que se desea
	 *                   encontrar. Si se pasa <b>null</b> en este parámetro se
	 *                   busca la {@link ProductoAccion} que esté visible para la
	 *                   {@link Company} Cliente sin importar su Verbo
	 * @return {@link List}<{@link ProductoAccion}>
	 */
	public Optional<ProductoAccion> findNotAcknowledgedFromProductBusinessAndAction(Long productId, Long businessId,
			String action) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ProductoAccion> criteria = builder.createQuery(ProductoAccion.class);
		Root<ProductoAccion> i = criteria.from(ProductoAccion.class);

		Predicate predicado = builder.and(builder.equal(i.get("empresaId"), businessId),
				builder.equal(i.get("eliminado"), false), builder.equal(i.get("recibido"), false));
		if (action != null)
			predicado = builder.and(predicado, builder.equal(i.get("accion"), action),
					builder.equal(i.get("productoId"), productId));
		criteria.select(i).where(predicado);

		TypedQuery<ProductoAccion> query = em.createQuery(criteria);
		List<ProductoAccion> listaAcciones = query.getResultList();
		if (listaAcciones.size() != 0)
			return Optional.of(listaAcciones.get(0));
		return Optional.ofNullable(null);
	}

}