package courier.uy.core.repository;

import courier.uy.core.entity.Precio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IPrecioRepository extends MongoRepository<Precio, String> {
    public void deleteAllByGlnListaVenta(String glnListaVenta);
    public List<Precio> findAllByProductoCppAndGlnListaVenta(String productoCpp, String glnListaVenta);
}
