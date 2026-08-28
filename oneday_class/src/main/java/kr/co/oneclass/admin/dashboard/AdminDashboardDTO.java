package kr.co.oneclass.admin.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardDTO {

	private int memberCount, writerCount, classCount, monthlySales;

	public AdminDashboardDTO() {
	}

	public AdminDashboardDomain toDomain() {
		return new AdminDashboardDomain(memberCount, writerCount, classCount, monthlySales);
	}
}
