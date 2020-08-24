package courier.uy.core.repository;

import courier.uy.core.entity.ReseteoContrasena;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IReseteoContrasenaRepository extends MongoRepository<ReseteoContrasena, String> {
    public ReseteoContrasena findFirstByOldId(long oldId);
}
