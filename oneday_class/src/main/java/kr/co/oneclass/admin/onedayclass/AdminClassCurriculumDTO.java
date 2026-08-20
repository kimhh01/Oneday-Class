package kr.co.oneclass.admin.onedayclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminClassCurriculumDTO {

	private int curriculumCode;
	private int classCode;
	private int stepNumber;
	private String curriculumTitle;
	private String curriculumDescription;
	private String curriculumImage;
}
