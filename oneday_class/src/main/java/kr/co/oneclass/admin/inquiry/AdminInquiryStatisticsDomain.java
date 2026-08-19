package kr.co.oneclass.admin.inquiry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminInquiryStatisticsDomain {
	private int totalCount;
	private int waitingCount;
	private int completedCount;
}
