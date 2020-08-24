package courier.uy.core.repository;

import courier.uy.core.entity.Company;
import courier.uy.core.entity.Grupo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IGrupoRepository extends MongoRepository<Grupo, String> {
    public Grupo findFirstByOldId(long oldId);
    public List<Grupo> findAllByOldId(long oldId);
    public Grupo findByNombreAndEmpresa(String nombre, Company empresa);
}
