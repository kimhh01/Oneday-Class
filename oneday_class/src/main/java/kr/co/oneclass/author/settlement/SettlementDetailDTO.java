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
public class SettlementDetailDTO {

    private int settlementCode;                                        // 정산 신청 코드
    private Date appliedAt;                                            // 정산 신청일
    private String settlementStatus;                                   // 정산 진행 상태
    private int totalPaymentAmount;                                    // 결제금액 합계
    private int totalFeeAmount;                                        // 수수료 합계
    private int settlementAmount;                                      // 정산금액
    private String businessName;                                       // 상호명
    private String authorName;                                         // 예금주 작가명
    private String bankName;                                           // 은행명
    private String accountNumber;                                      // 계좌번호
    private Date paidAt;                                               // 지급 완료일

    private List<SettlementTargetDTO> targetList = new ArrayList<>();  // 정산에 포함된 매출 목록
}
