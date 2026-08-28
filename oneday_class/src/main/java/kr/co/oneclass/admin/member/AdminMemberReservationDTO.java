package kr.co.oneclass.admin.member;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberReservationDTO {

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

	public AdminMemberReservationDomain toDomain() {

		return new AdminMemberReservationDomain(reservationCode, classCode, className, reservationDate, peopleNumber,
				reservationStatus, paymentCode, amount, means, paymentDate, paymentStatus, refund, refundDate);
	}

}