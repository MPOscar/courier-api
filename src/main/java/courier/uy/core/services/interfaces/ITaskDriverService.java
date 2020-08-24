package courier.uy.core.services.interfaces;

import courier.uy.core.entity.Task;

import java.util.List;

public interface ITaskDriverService {
	public Task save(Task task);
	public List<Task> getAllTasks();
}
