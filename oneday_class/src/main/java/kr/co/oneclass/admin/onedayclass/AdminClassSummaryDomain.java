package kr.co.oneclass.admin.onedayclass;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassSummaryDomain {

	private int classCode;
	private String className;

	private String region;
	private int runningTime;

	private String writerName;
	private String workshopName;
	private String categoryName;

	private String periodType;

	private Date recruitmentStartDate;
	private Date recruitmentEndDate;

	private int salePrice;
	private String classStatus;
}
