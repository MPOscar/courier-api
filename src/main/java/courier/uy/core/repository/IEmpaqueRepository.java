package courier.uy.core.repository;

import courier.uy.core.entity.Empaque;
import courier.uy.core.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IEmpaqueRepository extends MongoRepository<Empaque, String> {
    public Empaque findFirstByOldId(long oldId);
    public Optional<Empaque> findByIdAndEmpresa(String id, Company empresa);
    public Optional<Empaque> findBySidAndSempresa(String id, String sidEmpresa);
}
