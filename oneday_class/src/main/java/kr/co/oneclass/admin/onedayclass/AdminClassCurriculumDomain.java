package kr.co.oneclass.admin.onedayclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassCurriculumDomain {

	private int curriculumCode;
	private int classCode;
	private int stepNumber;
	private String curriculumTitle;
	private String curriculumDescription;
	private String curriculumImage;
}
