package courier.uy.core.repository;

import courier.uy.core.entity.Categoria;
import courier.uy.core.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ICategoriaRepository extends MongoRepository<Categoria, String> {
    public Categoria findFirstByOldId(long oldId);
    public List<Categoria> findAllByEmpresa(Company empresa);
    public List<Categoria> findAllBySempresa(String sidEmpresa);
}
