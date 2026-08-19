package kr.co.oneclass.admin.inquiry;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminInquirySummaryDomain {
	private int inquiryCode;
	private String inquiryTypeName;
	private String title;
	private String writerName;
	private String writerType;
	private Date inquiryDate;
	private String answerStatus;
}
