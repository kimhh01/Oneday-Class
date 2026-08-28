package kr.co.oneclass.author.settlement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SettlementDashboardDTO {

    private int availableAmount;  // 지금 신청 가능한 정산금액
    private int waitingAmount;    // 아직 정산 가능일이 되지 않은 대기금액
    private int requestedAmount;  // 신청 진행중인 금액
    private int completedAmount;  // 지급 완료된 누적 정산금액
    private int completedCount;   // 지급 완료된 정산 그룹 수
    private int requestedCount;   // 처리 중인 정산 그룹 수
}
