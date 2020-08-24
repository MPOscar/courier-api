package courier.uy.core.db;

import courier.uy.core.entity.Tombola;
import courier.uy.core.repository.ITaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskDAO {

	@Autowired
    ITaskRepository taskRepository;

	private final MongoOperations mongoOperations;

	public TaskDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<Tombola> findAllSortByFechaTirada() {
		Query query = new Query();
		List<Tombola> tombolaList = mongoOperations.find(query.with(Sort.by(Sort.Direction.DESC, "fechaTirada")), Tombola.class);
		return tombolaList;
	}

}