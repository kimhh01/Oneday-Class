package kr.co.oneclass.author.settlement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SettlementApplyDTO {

    private int settlementCode;                                 // 정산 신청 코드
    private long authorCode;                                     // 신청 작가 코드
    private List<Integer> paymentCodeList = new ArrayList<>();  // 신청에 포함할 결제 코드 목록
    private int totalPaymentAmount;                             // 신청 결제금액 합계
    private int totalFeeAmount;                                 // 수수료 합계
    private int settlementAmount;                               // 최종 정산 신청금액
    private String businessName;                                // 상호명
    private String authorName;                                  // 예금주 작가명
    private String bankName;                                    // 은행명
    private String accountNumber;                               // 계좌번호
    private String settlementStatus;                            // 정산 상태
    private Date appliedAt;                                     // 정산 신청일
}
