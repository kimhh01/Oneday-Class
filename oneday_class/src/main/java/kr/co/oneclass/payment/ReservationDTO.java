package kr.co.oneclass.payment;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReservationDTO {
	private int reservationCode;   // 예약 코드
    private int classCode;         // 클래스 코드
    private int memberCode;        // 회원 코드
    private int scheduleCode;      // 스케줄 코드
    private Date reservationDate;  // 예약일
    private int peopleNumber;      // 예약 인원
    private String status;         // 예약 상태
    private int totalPrice;        // 총 결제 금액
}
