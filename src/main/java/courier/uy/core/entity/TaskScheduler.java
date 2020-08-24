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

@Document(collection = "TaskScheduler")
public class TaskScheduler extends Entidad {

	private int repeatEach;

	private String intervalTime;

	private String monthOption;

	private int monday;

	private int tuesday;

	private int wednesday;

	private int thursday;

	private int friday;

	private int saturday;

	private int sunday;

	private String finish;

	private int finishAfterRepetitions;

	private int repetitions;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	protected DateTime finishDate;

	public int getRepeatEach() {
		return repeatEach;
	}

	public void setRepeatEach(int repeatEach) {
		this.repeatEach = repeatEach;
	}

	public String getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(String intervalTime) {
		this.intervalTime = intervalTime;
	}

	public String getMonthOption() {
		return monthOption;
	}

	public void setMonthOption(String monthOption) {
		this.monthOption = monthOption;
	}

	public int getMonday() {
		return monday;
	}

	public void setMonday(int monday) {
		this.monday = monday;
	}

	public int getTuesday() {
		return tuesday;
	}

	public void setTuesday(int tuesday) {
		this.tuesday = tuesday;
	}

	public int getWednesday() {
		return wednesday;
	}

	public void setWednesday(int wednesday) {
		this.wednesday = wednesday;
	}

	public int getThursday() {
		return thursday;
	}

	public void setThursday(int thursday) {
		this.thursday = thursday;
	}

	public int getFriday() {
		return friday;
	}

	public void setFriday(int friday) {
		this.friday = friday;
	}

	public int getSaturday() {
		return saturday;
	}

	public void setSaturday(int saturday) {
		this.saturday = saturday;
	}

	public int getSunday() {
		return sunday;
	}

	public void setSunday(int sunday) {
		this.sunday = sunday;
	}

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}

	public int getFinishAfterRepetitions() {
		return finishAfterRepetitions;
	}

	public void setFinishAfterRepetitions(int finishAfterRepetitions) {
		this.finishAfterRepetitions = finishAfterRepetitions;
	}

	public int getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}

	public DateTime getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(DateTime finishDate) {
		this.finishDate = finishDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskScheduler other = (TaskScheduler) obj;
		if (other.getId().equals(this.getId()))
			return true;
		return false;
	}
}
