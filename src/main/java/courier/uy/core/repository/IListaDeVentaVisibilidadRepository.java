package courier.uy.core.repository;

import courier.uy.core.entity.ListaDeVentaVisibilidad;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IListaDeVentaVisibilidadRepository extends MongoRepository<ListaDeVentaVisibilidad, String> {
    public ListaDeVentaVisibilidad findFirstByOldId(long oldId);
}
