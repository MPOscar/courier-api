package courier.uy.core.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import courier.uy.core.entity.Presentacion;
import courier.uy.core.repository.IPresentacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class PresentacionesDAO {

	@Autowired
    IPresentacionRepository presentacionRepository;

	@PersistenceContext
	private EntityManager em;

	private final MongoOperations mongoOperations;

	public PresentacionesDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<Presentacion> getAll() {
		CriteriaQuery<Presentacion> criteriaQuery = em.getCriteriaBuilder()
				.createQuery(Presentacion.class);
		criteriaQuery.from(Presentacion.class);

		return em.createQuery(criteriaQuery).getResultList();
	}

	public List<Presentacion> getVisible() {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Presentacion> criteria = builder.createQuery(Presentacion.class);
		Root<Presentacion> i = criteria.from(Presentacion.class);
		criteria.select(i).where(builder.equal(i.get("paraMostrar"), true));
		TypedQuery<Presentacion> query = em.createQuery(criteria);
		List<Presentacion> presentaciones = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("paraMostrar").is(true));
		List<Presentacion> presentaciones = mongoOperations.find(query, Presentacion.class);
		return presentaciones;
	}

	public Optional<Presentacion> findByName(String nombre) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Presentacion> criteria = builder.createQuery(Presentacion.class);
		Root<Presentacion> i = criteria.from(Presentacion.class);

		criteria.select(i).where(builder.equal(i.get("nombre"), nombre));

		TypedQuery<Presentacion> query = em.createQuery(criteria);

		List<Presentacion> presentaciones = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("nombre").is(nombre));
		List<Presentacion> presentaciones = mongoOperations.find(query, Presentacion.class);

		if (presentaciones.isEmpty()) {
			return Optional.ofNullable(null);
		}
		return Optional.of(presentaciones.get(0));
	}

	public Presentacion crear(Presentacion presentacion) {
		return presentacionRepository.save(presentacion);
	}

}