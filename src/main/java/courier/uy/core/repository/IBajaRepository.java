package courier.uy.core.repository;

import courier.uy.core.entity.Baja;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IBajaRepository extends MongoRepository<Baja, String> {
    public Baja findFirstByOldId(long oldId);
}
