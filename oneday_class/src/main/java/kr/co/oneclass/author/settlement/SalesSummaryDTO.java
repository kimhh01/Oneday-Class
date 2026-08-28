package kr.co.oneclass.author.settlement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesSummaryDTO {

    private int totalPaymentCount;         // 누적 결제 건수
    private int totalRefundCount;          // 누적 환불 건수
    private int totalPaymentAmount;        // 누적 결제금액
    private int totalCancelAmount;         // 누적 취소금액
    private int expectedSettlementAmount;  // 정산 예정금액
}
