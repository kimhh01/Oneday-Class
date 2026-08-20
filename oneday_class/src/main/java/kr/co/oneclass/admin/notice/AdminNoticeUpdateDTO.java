package kr.co.oneclass.admin.notice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNoticeUpdateDTO {
	private int noticeCode;
	private String noticeTitle, noticeType, noticeContent, status;
}
