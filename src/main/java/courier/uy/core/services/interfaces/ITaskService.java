package courier.uy.core.services.interfaces;

import courier.uy.core.entity.Task;
import courier.uy.core.entity.Tombola;

import java.util.List;
import java.util.Set;

public interface ITaskService {
	public Task save(Task task);
	public List<Task> getAllTasks();
}
