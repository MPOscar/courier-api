package courier.uy.core.repository;

import courier.uy.core.entity.Error;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IErrorRepository extends MongoRepository<Error, String> {
    public Error findFirstByOldId(long oldId);
}
