package kr.co.oneclass.author.settlement.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SettlementTargetDTO {

    private int paymentCode;          // 결제 코드
    private int reservationCode;      // 예약 코드
    private int classCode;            // 클래스 코드
    private String classTitle;        // 클래스명
    private int paymentAmount;        // 결제금액
    private int feeAmount;            // 수수료
    private int settlementAmount;     // 정산금액
    private Date availableDate;       // 정산 가능일
    private String settlementStatus;  // 정산 상태
}
