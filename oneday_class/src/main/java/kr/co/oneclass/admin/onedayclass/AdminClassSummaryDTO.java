package kr.co.oneclass.admin.onedayclass;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminClassSummaryDTO {

	private int classCode;
	private String className;

	private String region;
	private int runningTime;

	private String writerName;
	private String workshopName;
	private String categoryName;

	private Date recruitmentStartDate;
	private Date recruitmentEndDate;

	private int salePrice;
	private String approvalStatus;
	private String classStatus;
	
}
