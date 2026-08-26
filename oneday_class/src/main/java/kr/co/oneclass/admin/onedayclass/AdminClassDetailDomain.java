package kr.co.oneclass.admin.onedayclass;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassDetailDomain {

	private int classCode;
	private String className;

	private long writerCode;
	private String writerName;
	private String workshopName;

	private String categoryName;
	private String region;

	private int runningTime;
	private String classStatus;

	private int salePrice;
	private int marketPrice;
	private int discountRate;

	private Integer minimumPeople;
	private Integer maximumPeople;

	private Date recruitmentStartDate;
	private Date recruitmentEndDate;

	private String periodType;

	private String singleIntroduce;
	private String introduce;
	private String finishedProductDescription;

	private String approvalStatus;
	private String approvalMemo;
}
