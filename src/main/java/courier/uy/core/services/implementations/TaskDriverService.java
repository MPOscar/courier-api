package courier.uy.core.services.implementations;

import courier.uy.core.db.TaskDriverDAO;
import courier.uy.core.entity.Task;
import courier.uy.core.repository.ITaskRepository;
import courier.uy.core.services.interfaces.ITaskDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDriverService implements ITaskDriverService {

	@Autowired
	private ITaskRepository taskRepository;

	@Autowired
	private TaskDriverDAO taskDriverDAO;

	public TaskDriverService(ITaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public Task save(Task task){
		return taskRepository.save(task);
	}

	public List<Task> getAllTasks(){
		return taskDriverDAO.findAll();
	}
}
