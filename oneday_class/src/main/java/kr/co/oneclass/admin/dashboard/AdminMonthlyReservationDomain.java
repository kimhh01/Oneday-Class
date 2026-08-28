package kr.co.oneclass.admin.dashboard;

import lombok.Getter;

@Getter
public class AdminMonthlyReservationDomain {

	private String month;
	private int reservationCount;

	public AdminMonthlyReservationDomain(String month, int reservationCount) {

		this.month = month;
		this.reservationCount = reservationCount;
	}
	
}
