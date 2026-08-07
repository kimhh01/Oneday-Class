package kr.co.oneclass.author.settlement.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesListDTO {

    private int paymentCode;       // 결제 코드
    private int reservationCode;   // 예약 코드
    private int classCode;         // 클래스 코드
    private String classTitle;     // 클래스명
    private String memberName;     // 구매자명
    private Date scheduleDate;     // 수업 진행일
    private int paymentAmount;     // 결제금액
    private int discountAmount;    // 할인금액
    private int settlementAmount;  // 정산금액
    private String paymentStatus;  // 결제 상태
    private Date paymentDate;      // 결제일
}
