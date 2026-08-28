package kr.co.oneclass.classDetail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DetailInfoDTO {
	private int detailInfoCode;
	private int classCode;
	private String character;
	private String ingredients;
}
