package courier.uy.core.repository;

import courier.uy.core.entity.Param;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IParamRepository extends MongoRepository<Param, String> {
    public Param findFirstByNombre(String nombre);
}
