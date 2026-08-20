package kr.co.oneclass.admin.inquiry;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInquirySummaryDTO {
	private int inquiryCode;
	private String inquiryTypeName;
	private String title;
	private String writerName;
	private String writerType;
	private Date inquiryDate;
	private String answerStatus;
}
