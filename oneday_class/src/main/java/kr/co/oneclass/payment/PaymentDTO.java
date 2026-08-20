package kr.co.oneclass.payment;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentDTO {
	private int paymentCode;
	private int reservationCode;
	private int amount;
	private String means;
	private Date paymentDate;
	private String status;
	private String pgCode;
	private int refund;
	private Date refundDate;
}
