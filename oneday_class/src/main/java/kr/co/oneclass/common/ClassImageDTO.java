package kr.co.oneclass.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClassImageDTO {
	private int classImgCode;
	private int classCode;
	private String type;
	private String image;
	private int sortOrder;
}
