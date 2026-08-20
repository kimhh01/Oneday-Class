package kr.co.oneclass.admin.notice;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNoticeDetailDTO {
	private int noticeCode, managerCode;
	private String noticeTitle, noticeType, noticeContent, writerId, status;
	private Date registeredDate;
}
