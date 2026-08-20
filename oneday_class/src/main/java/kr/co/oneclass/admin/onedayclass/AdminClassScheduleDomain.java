package kr.co.oneclass.admin.onedayclass;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassScheduleDomain {

	private int scheduleCode;
	private int classCode;
	private Date classDate;
	private String startTime;
	private String endTime;
	private int minimumPeople;
	private int maximumPeople;
}
