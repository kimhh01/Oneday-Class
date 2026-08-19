package kr.co.oneclass.admin.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInquiryAnswerDTO {
	private int inquiryCode;
	private Integer managerCode;
	private String answer;
}
