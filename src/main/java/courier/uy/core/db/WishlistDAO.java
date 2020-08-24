package courier.uy.core.db;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Wishlist;
import courier.uy.core.repository.IWishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class WishlistDAO {

	@Autowired
    IWishlistRepository wishlistRepository;

	@PersistenceContext
	private EntityManager em;

	private final MongoOperations mongoOperations;

	public WishlistDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public Wishlist findById(String id) {
		return wishlistRepository.findById(id).get();
	}

	public Wishlist save(Wishlist toAdd) {
		toAdd.setFechaEdicion();
		toAdd = wishlistRepository.save(toAdd);
		toAdd.setSId(toAdd.getId());
		return wishlistRepository.save(toAdd);
	}

	public void update(Wishlist toUpdate) {
		toUpdate.setFechaEdicion();
		wishlistRepository.save(toUpdate);
	}

	public List<Wishlist> findByKey(String key, String value) {
		Query query = new Query();
		List<Wishlist> wishlists = mongoOperations.find(query.addCriteria(Criteria.where(key).is(value)), Wishlist.class);
		return wishlists;
	}

	public Wishlist findByProductAndBusiness(String businessId, String productId) {
		Query query = new Query();
		List<Wishlist> wishlists = mongoOperations.find(query.addCriteria(Criteria.where("sempresa").is(businessId)), Wishlist.class);

		if (wishlists != null && wishlists.size() > 0)
			return wishlists.get(0);
		else
			return null;
	}

	public Wishlist findByProductBusiness(String businessId) {
		Query query = new Query();
		List<Wishlist> wishlists = mongoOperations.find(query.addCriteria(Criteria.where("sempresa").is(businessId)), Wishlist.class);
		if (wishlists != null && wishlists.size() > 0)
			return wishlists.get(0);
		else
			return null;
	}

	/**
	 * 
	 * Devuelve la cantidad de elementos en la Wishilist de una
	 * {@link Company} proveedor para una {@link Company} cliente
	 * 
	 * @param idProveedor
	 * @param idCliente
	 * @return
	 */
	public BigInteger cantidadElementosWishlist(String idProveedor, String idCliente) {
		@SuppressWarnings("unchecked")
		/*TypedQuery<BigInteger> queryContar = (TypedQuery<BigInteger>) em.createNamedQuery("contarCantidadEnWishlist");
		queryContar.setParameter("proveedor", idProveedor);
		queryContar.setParameter("cliente", idCliente);
		BigInteger total = queryContar.getSingleResult();*/
		BigInteger total = BigInteger.valueOf(0);
		return total;
	}

	public List<Wishlist> getAll(){
		return wishlistRepository.findAll();
	}

}
