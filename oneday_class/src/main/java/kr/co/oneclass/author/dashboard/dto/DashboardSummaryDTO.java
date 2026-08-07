package kr.co.oneclass.author.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardSummaryDTO {

    private int recruitingClassCount;       // 모집중인 클래스 수
    private int unansweredInquiryCount;     // 미답변 문의 수
    private int todayReservationCount;      // 오늘 예약 수
    private int availableSettlementAmount;  // 정산 가능금액
}
