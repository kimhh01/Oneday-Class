package kr.co.oneclass.admin.notice;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNoticeSummaryDTO {
	private int noticeCode, managerCode;
	private String noticeTitle, noticeType, writerId, status;
	private Date registeredDate;
}
