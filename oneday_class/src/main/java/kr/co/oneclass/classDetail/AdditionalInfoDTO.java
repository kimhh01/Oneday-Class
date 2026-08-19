package kr.co.oneclass.classDetail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AdditionalInfoDTO {
	private int additionalInfoCode;
	private int classCode;
	private String additionalInfoContent;
}
