package kr.co.oneclass.admin.onedayclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminClassSearchDTO {

	private String keyword;
	private String approvalStatus;
	private String classStatus;

	private int page = 1;
	private int pageSize = 10;
	private int startRow;
	private int endRow;
}
