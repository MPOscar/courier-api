package courier.uy.core.repository;

import courier.uy.core.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ITaskRepository extends MongoRepository<Task, String> {
}
