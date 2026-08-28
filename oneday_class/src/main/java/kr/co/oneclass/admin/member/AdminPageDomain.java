package kr.co.oneclass.admin.member;

import lombok.Getter;

@Getter
public class AdminPageDomain {
	private int currentPage, totalCount, pageSize, totalPage;

	public AdminPageDomain(int currentPage, int totalCount, int pageSize) {
		this.currentPage = currentPage;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.totalPage = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / pageSize);
	}

}
