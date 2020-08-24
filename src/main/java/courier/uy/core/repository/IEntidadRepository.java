package courier.uy.core.repository;

import courier.uy.core.entity.Entidad;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IEntidadRepository extends MongoRepository<Entidad, String> {
}
