package kr.co.oneclass.admin.onedayclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassAdditionalInfoDomain {

	private int additionalInfoCode;
	private int classCode;
	private String content;
}
