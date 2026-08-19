package kr.co.oneclass.admin.notice;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AdminNoticeSummaryDomain {
	private int noticeCode, managerCode;
	private String noticeTitle, noticeType, writerId, status;
	private Date registeredDate;
}
