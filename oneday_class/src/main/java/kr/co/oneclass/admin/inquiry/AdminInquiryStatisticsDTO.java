package kr.co.oneclass.admin.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInquiryStatisticsDTO {
	private int totalCount;
	private int waitingCount;
	private int completedCount;
}
