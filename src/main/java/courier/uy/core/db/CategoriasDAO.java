package courier.uy.core.db;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Company;
import courier.uy.core.resources.dto.PaginadoRequest;
import courier.uy.core.exceptions.ModelException;
import courier.uy.core.repository.ICategoriaRepository;
import courier.uy.core.repository.IEmpresaRepository;
import courier.uy.core.resources.dto.PaginadoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class CategoriasDAO {

	@PersistenceContext
	private EntityManager em;

	@Autowired
    ICategoriaRepository categoriaRepository;

	@Autowired
    IEmpresaRepository empresaRepository;

	private final MongoOperations mongoOperations;

	public CategoriasDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<Categoria> getAll() {
		return categoriaRepository.findAll();
	}

	public Optional<Categoria> findByName(String nombre, Company empresa) {
		/*em.clear();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> criteria = builder.createQuery(Categoria.class);
		Root<Categoria> i = criteria.from(Categoria.class);
		criteria.select(i).where(builder.equal(i.get("nombre"), nombre));
		TypedQuery<Categoria> query = em.createQuery(criteria);
		List<Categoria> categorias = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("nombre").is(nombre));
		List<Categoria> categorias = mongoOperations.find(query, Categoria.class);

		for (Categoria c : categorias) {
			if (c.getEmpresa() != null && (c.getEmpresa().getId() == empresa.getId()))
				return Optional.of(c);
		}

		return Optional.ofNullable(null);

	}

	public Optional<Categoria> findByNameAndParent(String nombre, Company empresa, String parentName) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> criteria = builder.createQuery(Categoria.class);
		Root<Categoria> i = criteria.from(Categoria.class);
		criteria.select(i).where(builder.equal(i.get("nombre"), nombre));
		TypedQuery<Categoria> query = em.createQuery(criteria);
		List<Categoria> categorias = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("nombre").is(nombre));
		List<Categoria> categorias = mongoOperations.find(query, Categoria.class);

		for (Categoria c : categorias) {
			if (c.getEmpresa().getId() == empresa.getId()) {
				if (c.getPadre() != null) {
					if (c.getPadre().getNombre().equals(parentName)) {
						return Optional.of(c);
					}
				}
			}
		}

		return Optional.ofNullable(null);

	}

	/**
	 * Devuelve un {@link Optional} de tipo {@link Categoria} con la
	 * {@link Categoria} con el <b>null</null> pasado por parámetros.
	 * 
	 * @param id
	 * @return {@link Optional} de tipo {@link Categoria}
	 */
	public Optional<Categoria> findById(String id) {
		return categoriaRepository.findById(id);
	}

	public void update(Categoria toUpdate) {
		toUpdate.setFechaEdicion();
		categoriaRepository.save(toUpdate);
	}

	public Categoria crear(Categoria categoria, Company empresa) {
		categoria.setEmpresa(empresa);
		categoria.setSempresa(empresa.getSId());
		categoria = categoriaRepository.save(categoria);
		categoria.setSId(categoria.getId());
		categoriaRepository.save(categoria);
		return categoria;
	}

	public Categoria insert(Categoria c) {
		c.setFechaCreacion();
		c.setFechaEdicion();
		c = categoriaRepository.save(c);
		c.setSId(c.getId());
		categoriaRepository.save(c);
		return c;
	}

	public List<Categoria> findByKey(String key, String value) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> criteria = builder.createQuery(Categoria.class);
		Root<Categoria> i = criteria.from(Categoria.class);
		criteria.select(i).where(builder.equal(i.get(key), value), builder.equal(i.get("eliminado"), false));
		TypedQuery<Categoria> query = em.createQuery(criteria);
		List<Categoria> listaCategorias = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where(key).is(value));
		List<Categoria> categorias = mongoOperations.find(query, Categoria.class);

		return categorias;
	}

	/**
	 * Devuelve las {@link Categoria} paginadas que pertenezcan a la {@link Company}
	 * pasada por parámetros
	 * 
	 * @param paginado
	 * @param idProveedor
	 * @return
	 * @throws ModelException
	 */
	public PaginadoResponse<List<Categoria>> obtenerCategoriasEmpresa(PaginadoRequest paginado, String idProveedor)
			throws ModelException {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> criteria = builder.createQuery(Categoria.class);
		Root<Categoria> i = criteria.from(Categoria.class);
		Predicate predicado = builder.and(Utils.predicadoEliminado(builder, i),
				builder.equal(i.get("empresa"), idProveedor));
		criteria.select(i).where(predicado);
		Session session = em.unwrap(Session.class);
		return Utils.paginar(paginado, criteria, builder, i, session);*/

		Optional<Company> empresa = this.empresaRepository.findById(idProveedor);
		List<Categoria> categorias = this.categoriaRepository.findAllBySempresa(empresa.get().getSId());
		return new PaginadoResponse((long)1, (long)1, (long)1, categorias);
	}

	public List<Categoria> getAllByEmpresa(Company empresa) {
		/*CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Categoria> criteria = builder.createQuery(Categoria.class);
		Root<Categoria> i = criteria.from(Categoria.class);
		criteria.select(i).where(builder.equal(i.get("empresa"), empresa), builder.equal(i.get("eliminado"), false));
		TypedQuery<Categoria> query = em.createQuery(criteria);
		List<Categoria> categorias = query.getResultList();*/

		Query query = new Query();
		query.addCriteria(Criteria.where("sempresa").is(empresa.getSId()));
		List<Categoria> categorias = mongoOperations.find(query, Categoria.class);

		return categorias;
	}

}
