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
    private int todayApplicantCount;          // 오늘 새로 신청한 인원
    private int yesterdayApplicantCount;      // 어제 새로 신청한 인원
    private int todayVisitorCount;            // 오늘 수업 일정에 예약된 인원
    private int yesterdayVisitorCount;        // 어제 수업 일정에 예약된 인원
    private long availableSettlementAmount;   // 정산 가능금액
    private int availableSettlementCount;     // 정산 가능한 결제 건수
}
