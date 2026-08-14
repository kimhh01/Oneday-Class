package kr.co.oneclass.author.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardSummaryDTO {

    private int recruitingClassCount;        // 모집중인 클래스 수
    private int totalClassCount;              // 전체 운영 클래스 수
    private int unansweredInquiryCount;       // 미답변 문의 수
    private double averageResponseDays;       // 답변 완료 문의의 평균 응답일
    private int todayReservationCount;        // 오늘 예약 인원
    private int yesterdayReservationCount;    // 어제 예약 인원
    private long availableSettlementAmount;   // 정산 가능금액
    private int availableSettlementCount;     // 정산 가능한 결제 건수
}
