package courier.uy.core.repository;

import courier.uy.core.entity.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IRolRepository extends MongoRepository<Rol, String> {
    public Rol findFirstByOldId(long oldId);
}
