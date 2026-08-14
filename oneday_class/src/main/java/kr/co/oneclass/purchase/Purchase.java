package kr.co.oneclass.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

	// 1. CLASS 테이블 매핑
    private int classCode;             // class_code
    private int categoryCode;          // category_code
    private String className;           // name (클래스명)
    private int classPrice;            // price
    private String address;             // address
    private String address2;            // address2
    private String singleIntroduce;     // single_introduce
    private String introduce;           // introduce
    private String classImg;            // 썸네일 이미지

    // 2. RESERVATION 테이블 매핑
    private int reservationCode;        // reservation_code
    private int memberCode;             // member_code
    private int scheduleCode;           // schedule_code
    private String reservationDate;     // date
    private int reservationNum;         // people_number
    private String classStatus;         // status (예약/수강 상태)
    private int totalPrice;             // price

    // 3. PAYMENT 테이블 매핑
    private int paymentCode;            // payment_code
    private int finalPrice;             // amount (결제 금액)
    private String paymentMethod;       // means (결제 수단)
    private String paymentDate;         // payment_date
    private String paymentStatus;       // status (결제 상태)
    private String pgCode;              // PG_code
    private Integer refund;             // refund
    private String refundDate;          // refund_date
    
    // 4. CREATOR 테이블 매핑 
    private Long operatorCode;			// 작가 코드
    private String activityName;		// 작가 이름

    // HTML(purchase.html) 바인딩 호환용 Getter
    public String getStatus() {
        return this.classStatus != null ? this.classStatus : this.paymentStatus;
    }

    public String getClassAddr() {
        if (address2 != null && !address2.isEmpty()) {
            return address + " " + address2;
        }
        return address;
    }
}
