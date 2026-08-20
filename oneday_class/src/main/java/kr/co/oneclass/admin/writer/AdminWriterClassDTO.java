package kr.co.oneclass.admin.writer;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminWriterClassDTO {

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
