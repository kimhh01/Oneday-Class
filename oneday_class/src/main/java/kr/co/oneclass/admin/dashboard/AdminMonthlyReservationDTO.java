package kr.co.oneclass.admin.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMonthlyReservationDTO {

	private String month;
	private int reservationCount;

	public AdminMonthlyReservationDTO() {
	}

	public AdminMonthlyReservationDomain toDomain() {
		return new AdminMonthlyReservationDomain(month, reservationCount);
	}
}
