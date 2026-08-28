package kr.co.oneclass.admin.dashboard;

import lombok.Getter;

@Getter
public class AdminDashboardDomain {

	private int memberCount, writerCount, classCount, monthlySales;

	public AdminDashboardDomain(int memberCount, int writerCount, int classCount, int monthlySales) {
		this.memberCount = memberCount;
		this.writerCount = writerCount;
		this.classCount = classCount;
		this.monthlySales = monthlySales;
	}
}
