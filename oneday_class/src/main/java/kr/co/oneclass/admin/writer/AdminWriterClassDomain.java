package kr.co.oneclass.admin.writer;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminWriterClassDomain {

	private int classCode;
	private String className;
	private String periodType;

	private Date recruitmentStartDate;
	private Date recruitmentEndDate;

	private Integer minimumPeople;
	private Integer maximumPeople;

	private int price;
	private String classStatus;
}
