package kr.co.oneclass.admin.onedayclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminClassImageDomain {

	private int imageCode;
	private int classCode;
	private String imageType;
	private String imagePath;
	private int imageOrder;
}
