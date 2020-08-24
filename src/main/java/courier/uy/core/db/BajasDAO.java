package courier.uy.core.db;

import courier.uy.core.entity.Baja;
import courier.uy.core.repository.IBajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class BajasDAO {

	@PersistenceContext
	private EntityManager em;

	@Autowired
    IBajaRepository bajaRepository;

	public Baja findById(String id) {
		return bajaRepository.findById(id).get();
	}

	public Baja insert(Baja toAdd) {
		toAdd.setFechaCreacion();
		toAdd.setFechaEdicion();
		return bajaRepository.save(toAdd);
	}

	public void update(Baja toUpdate) {
		toUpdate.setFechaEdicion();
		bajaRepository.save(toUpdate);
	}

}
