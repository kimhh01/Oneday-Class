package kr.co.oneclass.admin.inquiry;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminInquiryDetailDomain {
	private int inquiryCode;
	private int inquiryTypeCode;
	private String inquiryTypeName;
	private Integer managerCode;
	private Long operatorCode;
	private Integer memberCode;
	private String writerName;
	private String writerType;
	private String title;
	private String content;
	private String inquiryImg;
	private Date inquiryDate;
	private String answer;
	private Date answerDate;
	private String answerStatus;
}
