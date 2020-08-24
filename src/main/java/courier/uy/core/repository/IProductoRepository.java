package courier.uy.core.repository;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface IProductoRepository extends MongoRepository<Producto, String> {
    public Producto findFirstByOldId(long oldId);

    public List<Producto> findAllBySgruposConVisibilidadIn(Set<String> gruposConVisibilidad);
    public List<Producto> findAllBySempresasConVisibilidadIn(String empresaId);
    public List<Producto> findAllBySgruposConVisibilidadInOrSempresasConVisibilidadIn(Set<String> gruposConVisibilidad, String empresaId);
    public List<Producto> findAllByEsPublicoAndSempresaNot(boolean esPublico, String empresaId);
    public List<Producto> findAllByEmpresa(Company empresa);
    public List<Producto> findAllBySempresa(String empresaId);

    public List<Producto> findAllByDescripcionLike(List<String> descripcion);

}
