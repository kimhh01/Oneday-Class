package kr.co.oneclass.admin.notice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNoticeCreateDTO {
	private String noticeTitle, noticeType, noticeContent;
	private int managerCode;
	private String status = "비공개";
}
