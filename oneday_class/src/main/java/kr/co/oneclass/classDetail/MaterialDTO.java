package kr.co.oneclass.classDetail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MaterialDTO {
	private int materialCode;
	private int classCode;
	private String materialName;
	private String materialContent;
}
