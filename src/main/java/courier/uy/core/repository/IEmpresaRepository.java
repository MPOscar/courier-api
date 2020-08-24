package courier.uy.core.repository;

import courier.uy.core.entity.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface IEmpresaRepository extends MongoRepository<Company, String> {
    public Company findFirstByOldId(long oldId);
    public List<Company> findAllByIdNot(String id);
    public List<Company> findAllByIdNot(String id, Pageable pageablet);
    public List<Company> findAllBySidIn(Set<String> sidEmpresas);
}
