package courier.uy.core.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.Error;
import courier.uy.core.repository.IErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ErrorsDAO {

	@PersistenceContext
	private EntityManager em;

	@Autowired
    IErrorRepository errorRepository;

	public List<Error> getAll() {
		CriteriaQuery<Error> criteriaQuery = em.getCriteriaBuilder().createQuery(Error.class);
		criteriaQuery.from(Error.class);
		return em.createQuery(criteriaQuery).getResultList();
	}

	public Error insert(String message, Usuario usuario) {
		Error error = new Error(message, usuario);
		return errorRepository.save(error);
	}

	public Error insert(String message) {
		Error error = new Error(message, "");
		return errorRepository.save(error);
	}

	public Error insert(String message, String stackTrace) {
		Error error = new Error(message, stackTrace);
		return errorRepository.save(error);
	}
}
