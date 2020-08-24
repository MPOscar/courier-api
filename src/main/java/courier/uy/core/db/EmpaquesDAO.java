package courier.uy.core.db;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import courier.uy.core.entity.Presentacion;
import courier.uy.core.entity.UsuarioEmpresa;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.repository.IEmpaqueRepository;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.resources.dto.PaginadoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class EmpaquesDAO {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(EmpaquesDAO.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private IEmpresaRepository empresaRepository;

	@Autowired
	private IEmpaqueRepository empaqueRepository;

	private final MongoOperations mongoOperations;

	public EmpaquesDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Optional<Empaque> findById(String id) throws ModelException {
		return this.findById(id, null);
	}

	/**
	 * <p>
	 * Devuelve el {@link Empaque} con el Id pasado por parámetros.
	 * </p>
	 * <p>
	 * Para encontrarlo verifica dos condiciones:
	 * <p>
	 * - Si viene un {@link UsuarioEmpresa} verifica que el {@link Empaque}
	 * efectivamente pertenezca a la {@link Company} pasada por parámetros
	 * </p>
	 * <p>
	 * - Si no viene un {@link UsuarioEmpresa} devuelve cualquier {@link Empaque}
	 * que tenga el Id pasado por parámetros
	 * </p>
	 * </p>
	 * 
	 * @param id
	 * @param ue
	 * @return
	 * @throws ModelException
	 */
	public Optional<Empaque> findById(String id, UsuarioEmpresa ue) throws ModelException {
		if (id == null)
			throw new ModelException("Debe especificar un Id válido para seleccionar un Empaque");

		Optional<Empaque> empaque;

		if (ue != null) {
			empaque = empaqueRepository.findBySidAndSempresa(id, ue.getEmpresa().getSId());
		}else{
			empaque = empaqueRepository.findById(id);
		}

		return empaque;
	}

	public PaginadoResponse<List<Empaque>> getAll(PaginadoRequest paginado, UsuarioEmpresa ue) throws ModelException {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Empaque> criteria = builder.createQuery(Empaque.class);
		Root<Empaque> root = criteria.from(Empaque.class);
		Predicate criterioSeleccion = Utils.predicadoEliminado(builder, root);
		if (ue != null)
			criterioSeleccion = builder.and(criterioSeleccion,
					builder.equal(root.get("empresa"), ue.getEmpresa().getId()));
		criteria.select(root).where(criterioSeleccion);

		Session session = em.unwrap(Session.class);
		PaginadoResponse<List<Empaque>> paginadoResponse = Utils.paginar(paginado, criteria, builder, root,
				session);*/

		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(ue.getEmpresa().getSId()));
		List<Empaque> empaques = mongoOperations.find(query, Empaque.class);

		PaginadoResponse<List<Empaque>> paginadoResponse = new PaginadoResponse<>();
		paginadoResponse.setElementos(empaques);
		paginadoResponse.setTotal((long)empaques.size());
		return paginadoResponse;
	}

	public Optional<Set<Empaque>> obtenerEmpaquesPorEmpresa(Company empresa) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Empaque> criteria = builder.createQuery(Empaque.class);
		Root<Empaque> i = criteria.from(Empaque.class);
		criteria.select(i).where(builder.equal(i.get("empresa"), empresa.getId()),
				Utils.predicadoEliminado(builder, i));
		TypedQuery<Empaque> query = em.createQuery(criteria);
		List<Empaque> empaques = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresa.getSId()));
		List<Empaque> empaques = mongoOperations.find(query, Empaque.class);

		return Optional.ofNullable(new HashSet<Empaque>(empaques));

	}

	public Optional<Empaque> findByCppAndBusiness(String cpp, Company empresa) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Empaque> criteria = builder.createQuery(Empaque.class);
		Root<Empaque> i = criteria.from(Empaque.class);
		criteria.select(i).where(builder.equal(i.get("cpp"), cpp), Utils.predicadoEliminado(builder, i));
		TypedQuery<Empaque> query = em.createQuery(criteria);
		List<Empaque> empaques = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("cpp").is(cpp));
		List<Empaque> empaques = mongoOperations.find(query, Empaque.class);

		for (Empaque em : empaques) {
			if (em.getEmpresa().getId() == empresa.getId())
				return Optional.of(em);
		}

		return Optional.ofNullable(null);

	}

	public Optional<Empaque> findByGtin(String gtin) {
		/*em.clear();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Empaque> criteria = builder.createQuery(Empaque.class);
		Root<Empaque> i = criteria.from(Empaque.class);
		criteria.select(i).where(builder.equal(i.get("gtin"), gtin), Utils.predicadoEliminado(builder, i));
		TypedQuery<Empaque> query = em.createQuery(criteria);*/

		Query query = new Query();
		query.addCriteria(Criteria.where("gtin").is(gtin));
		List<Empaque> empaques = mongoOperations.find(query, Empaque.class);

		if (empaques.size() != 0)
			return Optional.of(empaques.get(0));
		return Optional.ofNullable(null);

	}

	public Empaque save(Empaque empaque) {
		empaque = empaqueRepository.save(empaque);
		empaque.setSId(empaque.getId());
		return empaqueRepository.save(empaque);
	}

	public Empaque crear(Empaque empaque, Company empresa) {
		empaque.setEmpresa(empresa);
		empaque.setSempresa(empresa.getSId());
		if (empaque.getPadre() != null)
			empaque.getPadre().setEmpresa(empresa);
		empaque.setFechaCreacion();
		empaque.setFechaEdicion();
		empaque = empaqueRepository.save(empaque);
		empaque.setSId(empaque.getId());
		empaqueRepository.save(empaque);
		return empaque;
	}

	public Empaque crearConPresentacion(Empaque empaque, Company empresa, Presentacion presentacion) {
		empaque.setEmpresa(empresa);
		empaque.setSempresa(empresa.getSId());
		empaque.setPresentacion(presentacion);
		empaque.setFechaCreacion();
		empaque.setFechaEdicion();
		empaque = empaqueRepository.save(empaque);
		empaque.setSId(empaque.getId());
		empaqueRepository.save(empaque);
		return empaque;
	}

	public List<Empaque> getAll(){
		return empaqueRepository.findAll();
	}
}