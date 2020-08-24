package courier.uy.core.repository;

import courier.uy.core.entity.ProductoAccion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IProductoAccionRepository extends MongoRepository<ProductoAccion, String> {
    public ProductoAccion findFirstByOldId(long oldId);
}
