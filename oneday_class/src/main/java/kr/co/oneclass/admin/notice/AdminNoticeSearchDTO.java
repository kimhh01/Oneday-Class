package kr.co.oneclass.admin.notice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminNoticeSearchDTO {
	private String noticeType, status, keyword;
	private int page = 1, pageSize = 10, startRow, endRow;
}
