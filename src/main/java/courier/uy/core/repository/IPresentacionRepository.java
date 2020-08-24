package courier.uy.core.repository;

import courier.uy.core.entity.Presentacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IPresentacionRepository extends MongoRepository<Presentacion, String> {
    public Presentacion findFirstByNombre(String nombre);
}
