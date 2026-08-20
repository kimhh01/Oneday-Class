package kr.co.oneclass.admin.onedayclass;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminClassScheduleDTO {

	private int scheduleCode;
	private int classCode;
	private Date classDate;
	private String startTime;
	private String endTime;
	private int minimumPeople;
	private int maximumPeople;
}
