package kr.co.oneclass.admin.onedayclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminClassImageDTO {

	private int imageCode;
	private int classCode;
	private String imageType;
	private String imagePath;
	private int imageOrder;
}
