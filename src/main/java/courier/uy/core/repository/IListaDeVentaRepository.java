package courier.uy.core.repository;

import courier.uy.core.entity.ListaDeVenta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface IListaDeVentaRepository extends MongoRepository<ListaDeVenta, String> {
    public ListaDeVenta findFirstByOldId(long oldId);
    public List<ListaDeVenta> findAllBySempresaAndSempresasInOrSgruposIn(String proveedor, String empresa,  Set<String> grupos);

    public List<ListaDeVenta> findAllBySempresa(String proveedor);
}
