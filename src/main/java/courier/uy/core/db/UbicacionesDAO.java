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
import courier.uy.core.entity.Ubicacion;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.exceptions.ServiceException;
import courier.uy.core.repository.IUbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UbicacionesDAO {

	@Autowired
    IUbicacionRepository ubicacionRepository;

	@PersistenceContext
	private EntityManager em;

	public Ubicacion insert(Ubicacion e) throws ServiceException {
		if (this.existeUbicacion(e.getCodigo()).isPresent())
			throw new ServiceException("Ya existe la Ubicación pasada por parámetros");
		return ubicacionRepository.save(e);
	}

	public Ubicacion upsert(Ubicacion e) throws ServiceException {
		Optional<Ubicacion> optional = this.existeUbicacion(e.getCodigo());
		if (!optional.isPresent())
			return ubicacionRepository.save(e);

		Ubicacion ubiDB = optional.get();
		if (e.getEmpresa() != null && ubiDB.getEmpresa().getId() != e.getEmpresa().getId())
			throw new ServiceException("La Ubicación no pertenece a la Empresa actual");
		ubiDB.update(e);
		return ubicacionRepository.save(ubiDB);
	}

	public Ubicacion update(Ubicacion e) throws ServiceException {
		Optional<Ubicacion> optional = this.existeUbicacion(e.getCodigo());
		if (!optional.isPresent())
			throw new ServiceException("No existe la Ubicación a actualizar");
		Ubicacion ubiBD = optional.get();
		ubiBD.update(e);
		return ubicacionRepository.save(ubiBD);
	}

	public Ubicacion delete(Ubicacion e) throws ServiceException {
		Optional<Ubicacion> optional = this.existeUbicacion(e.getCodigo());
		if (!optional.isPresent())
			throw new ServiceException("No existe la Ubicación a eliminar");
		Ubicacion ubiBD = optional.get();
		ubiBD.eliminar();
		return ubicacionRepository.save(ubiBD);
	}

	public Ubicacion findById(String id) {
		return ubicacionRepository.findById(id).get();
	}

	public Optional<Ubicacion> existeUbicacion(String codigo) throws ServiceException {
		Optional<Ubicacion> ubicacion = this.findByKey("codigo", codigo);
		return ubicacion;
	}

	private Optional<Ubicacion> findByKey(String key, String value) {
		return this.findByKey(key, value, null);
	}

	public Optional<Ubicacion> findByKey(String key, String value, UsuarioEmpresa usuarioEmpresa) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Ubicacion> criteria = builder.createQuery(Ubicacion.class);
		Root<Ubicacion> i = criteria.from(Ubicacion.class);

		Predicate conditions = builder.and(builder.equal(i.get(key), value), builder.equal(i.get("eliminado"), false));
		if (usuarioEmpresa != null) {
			conditions = builder.and(conditions, builder.equal(i.get("empresa"), usuarioEmpresa.getEmpresa().getId()));
		}
		criteria.select(i).where(conditions);

		TypedQuery<Ubicacion> query = em.createQuery(criteria).setMaxResults(1);
		List<Ubicacion> listaUbicaciones = query.getResultList();

		if (listaUbicaciones.size() != 0)
			return Optional.of(listaUbicaciones.get(0));

		return Optional.ofNullable(null);
	}

	public List<Ubicacion> getAll() {
		return ubicacionRepository.findAll();
	}

	public List<Ubicacion> getAll(UsuarioEmpresa usuarioEmpresa) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Ubicacion> criteria = builder.createQuery(Ubicacion.class);
		Root<Ubicacion> i = criteria.from(Ubicacion.class);

		Predicate conditions = builder.equal(i.get("eliminado"), false);
		if (usuarioEmpresa != null) {
			conditions = builder.and(conditions, builder.equal(i.get("empresa"), usuarioEmpresa.getEmpresa().getId()));
		}
		criteria.select(i).where(conditions);

		TypedQuery<Ubicacion> query = em.createQuery(criteria);
		List<Ubicacion> listaUbicaciones = query.getResultList();

		return listaUbicaciones;
	}

	/**
	 * Devuelve un valor de tipo {@link Optional} que contiene un objeto de tipo
	 * {@link Ubicacion} si pertenece efectivamente el código pasado por parámetro a
	 * la {@link Company} pasada por parámetros
	 * 
	 * @param ubicacion
	 * @param empresa
	 * @param eliminado
	 * @return {@link Optional}<{@link Ubicacion}>: Optional que contiene la
	 *         {@link Ubicacion} si pertenece a la {@link Company} pasada por
	 *         parámetros
	 */
	public Optional<Ubicacion> perteneceEmpresa(Ubicacion ubicacion, Company empresa, Boolean eliminado) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Ubicacion> criteria = builder.createQuery(Ubicacion.class);
		Root<Ubicacion> i = criteria.from(Ubicacion.class);

		Predicate conditions = builder.equal(i.get("codigo"), ubicacion.getCodigo());
		if (eliminado != null) {
			conditions = builder.and(conditions, builder.equal(i.get("eliminado"), eliminado));
		}

		if (empresa != null) {
			conditions = builder.and(conditions, builder.equal(i.get("empresa"), empresa.getId()));
		}

		criteria.select(i).where(conditions);

		TypedQuery<Ubicacion> query = em.createQuery(criteria).setMaxResults(1);
		List<Ubicacion> listaUbicaciones = query.getResultList();

		if (listaUbicaciones.size() != 0)
			return Optional.of(listaUbicaciones.get(0));

		return Optional.ofNullable(null);
	}

	public List<Ubicacion> getAllByEmpresa(Company empresa) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Ubicacion> criteria = builder.createQuery(Ubicacion.class);
		Root<Ubicacion> i = criteria.from(Ubicacion.class);

		criteria.select(i).where(builder.equal(i.get("empresa"), empresa), builder.equal(i.get("eliminado"), false));

		TypedQuery<Ubicacion> query = em.createQuery(criteria);
		List<Ubicacion> ubicaciones = query.getResultList();
		return ubicaciones;
	}
}