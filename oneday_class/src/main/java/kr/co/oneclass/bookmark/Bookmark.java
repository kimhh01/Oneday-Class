package kr.co.oneclass.bookmark;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Bookmark {

	private int classCode;
	private String className, classRegion, classImg, classPrice;
}
