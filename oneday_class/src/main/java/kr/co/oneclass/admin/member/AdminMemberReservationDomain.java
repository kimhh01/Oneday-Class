package kr.co.oneclass.admin.member;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AdminMemberReservationDomain {

	private int reservationCode;
	private int classCode;
	private String className;

	private Date reservationDate;
	private int peopleNumber;
	private String reservationStatus;

	private Integer paymentCode;
	private Integer amount;
	private String means;
	private Date paymentDate;
	private String paymentStatus;

	private Integer refund;
	private Date refundDate;

}