package courier.uy.core.repository;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Usuario;
import courier.uy.core.entity.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IWishlistRepository extends MongoRepository<Wishlist, String> {
    public Wishlist findFirstByEmpresaAndUsuario(Company empresa, Usuario usuario);
    public Wishlist findFirstByEmpresa(Company empresa);
}
