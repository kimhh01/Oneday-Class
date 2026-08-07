package kr.co.oneclass.author.settlement.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SettlementListDTO {

    private int settlementCode;       // 정산 신청 코드
    private Date appliedAt;           // 정산 신청일
    private Date periodStartDate;     // 정산 대상 기간 시작일
    private Date periodEndDate;       // 정산 대상 기간 종료일
    private int paymentCount;         // 포함된 결제 건수
    private int totalPaymentAmount;   // 결제금액 합계
    private int totalFeeAmount;       // 수수료 합계
    private int settlementAmount;     // 정산금액
    private String settlementStatus;  // 정산 상태
    private String accountNumber;     // 현재 등록된 정산 계좌
    private Date paidAt;              // 지급 완료일
}
