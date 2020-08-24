package courier.uy.core.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.repository.IGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class GruposDAO {

	@PersistenceContext
	private EntityManager em;

	@Autowired
    IGrupoRepository grupoRepository;

	@Autowired
    IEmpresaRepository empresaRepository;

	public Grupo findById(String id) {
		Optional<Grupo> grupo = grupoRepository.findById(id);
		return grupo.isPresent() ? grupo.get() : null;
	}

	public Grupo insert(Grupo toAdd) {
		toAdd.setFechaCreacion();
		toAdd.setFechaEdicion();
		return grupoRepository.save(toAdd);
	}

	public void update(Grupo toUpdate) {
		toUpdate.setFechaEdicion();
		grupoRepository.save(toUpdate);
	}

	public Grupo save(Grupo toUpdate) {
		toUpdate.setFechaEdicion();
		return grupoRepository.save(toUpdate);
	}

	public List<Grupo> getAll() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Grupo> criteria = builder.createQuery(Grupo.class);
		Root<Grupo> i = criteria.from(Grupo.class);

		criteria.select(i).where(builder.equal(i.get("eliminado"), false));

		TypedQuery<Grupo> query = em.createQuery(criteria);
		List<Grupo> grupos = query.getResultList();
		return grupos;
	}

	public List<Grupo> getAllByEmpresa(Company empresa) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Grupo> criteria = builder.createQuery(Grupo.class);
		Root<Grupo> i = criteria.from(Grupo.class);

		criteria.select(i).where(builder.equal(i.get("empresa"), empresa), builder.equal(i.get("eliminado"), false));

		TypedQuery<Grupo> query = em.createQuery(criteria);
		List<Grupo> grupos = query.getResultList();
		return grupos;
	}

	public List<Grupo> findByKey(String key, String value) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Grupo> criteria = builder.createQuery(Grupo.class);
		Root<Grupo> i = criteria.from(Grupo.class);

		criteria.select(i).where(builder.equal(i.get(key), value), builder.equal(i.get("eliminado"), false));

		TypedQuery<Grupo> query = em.createQuery(criteria);
		List<Grupo> grupos = query.getResultList();
		return grupos;
	}

	public Grupo obtenerGrupo(String nombre, String empresaId) {
		Company empresa = empresaRepository.findById(empresaId).get();
		return grupoRepository.findByNombreAndEmpresa(nombre, empresa);
	}

}
