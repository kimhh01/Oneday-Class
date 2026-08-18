package kr.co.oneclass.admin.onedayclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassMaterialDomain {

	private int materialCode;
	private int classCode;
	private String materialName;
	private String materialContent;
}
