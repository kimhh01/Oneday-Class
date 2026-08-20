package kr.co.oneclass.map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RangeDTO {
	private int startNum;
	private int endNum;
	private String field;
	private String keyword;
}
