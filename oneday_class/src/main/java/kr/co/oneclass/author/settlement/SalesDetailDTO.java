package kr.co.oneclass.author.settlement;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesDetailDTO {

    private int paymentCode;
    private int reservationCode;
    private int classCode;
    private String classTitle;
    private int scheduleCode;
    private Date scheduleDate;
    private int memberCode;
    private String memberName;
    private String memberEmail;
    private String paymentMethod;
    private int paymentAmount;
    private int couponDiscountAmount;
    private int pointDiscountAmount;
    private int cancelAmount;
    private int cancelFeeAmount;
    private int platformFeeAmount;
    private int settlementAmount;
    private String settlementStatus;
    private String paymentStatus;
    private Date paymentDate;
}
