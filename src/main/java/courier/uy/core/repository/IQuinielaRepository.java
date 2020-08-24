package courier.uy.core.repository;

import courier.uy.core.entity.Quiniela;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IQuinielaRepository extends MongoRepository<Quiniela, String> {
    public Quiniela findFirstByFechaTirada(DateTime fechaTirada);
}
