package courier.uy.core.db;

import courier.uy.core.entity.Task;
import courier.uy.core.repository.ITaskRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskDriverDAO {

	@Autowired
    ITaskRepository taskRepository;

	private final MongoOperations mongoOperations;

	public TaskDriverDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public List<Task> findAll() {
		Query query = new Query();
		List<Task> taskList = mongoOperations.find(query.with(Sort.by(Sort.Direction.ASC, "fechaCreacion")), Task.class);
		List<Task> driverTasks = new ArrayList<>();
		for (Task task: taskList) {
			List<Task> createdTasks = new ArrayList<>();
			createdTasks = getAllTaskByIntervalTime(task);
			driverTasks.addAll(createdTasks);
		}
		return driverTasks;
	}

	public List<Task> getAllTaskByIntervalTime(Task task) {
		List<Task> tasks = new ArrayList<>();
		int repetitions = task.getTaskScheduler().getRepetitions();
		DateTime date = task.getFechaCreacion();
		DateTime finishDate;
		if(task.getTaskScheduler().getFinishDate() != null){
			finishDate =  task.getTaskScheduler().getFinishDate();
		}else{
			String stringDate = "31/12/" + new DateTime().getYear() + " 23:59:00";
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
			finishDate = formatter.parseDateTime(stringDate);
		}
		int count = 1;
		if (task.getTaskScheduler().getIntervalTime().equals("day") || task.getTaskScheduler().getIntervalTime().equals("week")) {
			tasks = createAllTaskByWeek(task, date, finishDate);
		} else  if (task.getTaskScheduler().getIntervalTime().equals("month")) {
			tasks = createAllTaskByMonth(task, date, finishDate);
		}
		return tasks;
	}

	public List<Task> createAllTaskByWeek(Task task, DateTime date, DateTime finishDate) {
		List<Task> tasks = new ArrayList<>();
		while (date.isBefore(finishDate)) {
			String weekDay = date.dayOfWeek().getAsText();
			switch (weekDay) {
				case "Sunday":
					if(task.getTaskScheduler().getSunday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
				case "Monday":
					if(task.getTaskScheduler().getMonday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
				case "Tuesday":
					if(task.getTaskScheduler().getTuesday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
				case "Wednesday":
					if(task.getTaskScheduler().getWednesday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
				case "Thursday":
					if(task.getTaskScheduler().getThursday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
				case "Friday":
					if(task.getTaskScheduler().getFriday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
				case "Saturday":
					if(task.getTaskScheduler().getSaturday() == 1){
						tasks.add(new Task(task, date));
					}
					break;
			}
			date = date.plusDays(1);
		}
		return tasks;
	}

	public Task addTask(Task task, DateTime taskDate){
		return new Task(task, taskDate);

	}

	public List<Task> createAllTaskByMonth(Task task, DateTime date, DateTime finishDate) {
		List<Task> tasks = new ArrayList<>();
        String monthOption = task.getTaskScheduler().getMonthOption();
		//while (date.isBefore(finishDate)) {
		//}
		return tasks;
	}

}