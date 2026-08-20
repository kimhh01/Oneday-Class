package kr.co.oneclass.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PageDomain {

	private int totalCount, currentPage, pageSize, totalPage, startPage, endPage;
}
