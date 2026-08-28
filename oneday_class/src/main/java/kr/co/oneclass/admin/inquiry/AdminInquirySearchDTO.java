package kr.co.oneclass.admin.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInquirySearchDTO {
	private String keyword;
	private Integer inquiryTypeCode;
	private String answerStatus;
	private int page = 1;
	private int pageSize = 10;
	private int startRow;
	private int endRow;
}
