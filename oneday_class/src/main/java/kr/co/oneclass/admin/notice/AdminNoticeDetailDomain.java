package kr.co.oneclass.admin.notice;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AdminNoticeDetailDomain {
	private int noticeCode, managerCode;
	private String noticeTitle, noticeType, noticeContent, writerId, status;
	private Date registeredDate;
}
