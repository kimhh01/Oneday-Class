package kr.co.oneclass.admin.writer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminWriterSearchDTO {

	private String keyword;
	private int page = 1;
	private int pageSize = 10;
	private int startRow;
	private int endRow;
}
