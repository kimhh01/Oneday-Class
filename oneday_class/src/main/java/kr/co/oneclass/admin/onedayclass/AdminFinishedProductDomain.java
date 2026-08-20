package kr.co.oneclass.admin.onedayclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminFinishedProductDomain {

	private int finishedProductCode;
	private int classCode;
	private String finishedProductImage;
}
