package kr.co.oneclass.board;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class RangeDTO {

	private int startNum, endNum;
	private String filed, keyword;
}
