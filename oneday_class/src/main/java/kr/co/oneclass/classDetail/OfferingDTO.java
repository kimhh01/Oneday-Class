package kr.co.oneclass.classDetail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OfferingDTO {
	private int classCode;
	private int offeringCode;
	private String offeringName;
	private String useYN;
}
