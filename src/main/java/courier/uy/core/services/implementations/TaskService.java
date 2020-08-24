package courier.uy.core.services.implementations;

import courier.uy.core.db.TaskDAO;
import courier.uy.core.entity.Task;
import courier.uy.core.repository.ITaskRepository;
import courier.uy.core.services.interfaces.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements ITaskService {

	@Autowired
	private ITaskRepository taskRepository;

	@Autowired
	private TaskDAO tombolaDAO;

	public TaskService(ITaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public Task save(Task task){
		return taskRepository.save(task);
	}

	public List<Task> getAllTasks(){
		return taskRepository.findAll();
	}
}
