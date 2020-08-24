package courier.uy.core.repository;

import courier.uy.core.entity.Ubicacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUbicacionRepository extends MongoRepository<Ubicacion, String> {
    public Ubicacion findFirstByOldId(long oldId);
}
