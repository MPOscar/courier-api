package courier.uy.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import courier.uy.core.utils.serializer.CustomDateTimeDeserializer;
import courier.uy.core.utils.serializer.CustomDateTimeSerializer;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "Task")
public class Task extends Entidad {

	private String comments;

	private String description;

	private String ipAddress;

	private String lat;

	private String lon;

	private String transType;

	private long pieces;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	protected DateTime taskDate;

	@DBRef(lazy = true)
	private Company company;

	private String scompany;

	private TaskScheduler taskScheduler;

	public Task(){
	}

	public Task(Task task, DateTime taskDate) {
		this.comments = task.getComments();
		this.description = task.getDescription();
		this.ipAddress = task.getIpAddress();
		this.lat = task.getLat();
		this.lon = task.getLon();
		this.transType = task.getTransType();
		this.pieces = task.getPieces();
		this.taskDate = taskDate;
		this.company = task.getCompany();
		this.scompany = task.getScompany();
		this.taskScheduler = task.getTaskScheduler();
		this.fechaCreacion = task.getFechaCreacion();
		this.fechaEdicion = task.getFechaEdicion();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public long getPieces() {
		return pieces;
	}

	public void setPieces(long pieces) {
		this.pieces = pieces;
	}

	public DateTime getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(DateTime taskDate) {
		this.taskDate = taskDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getScompany() {
		return scompany;
	}

	public void setScompany(String scompany) {
		this.scompany = scompany;
	}

	public TaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	public void setTaskScheduler(TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
