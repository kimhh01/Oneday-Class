	package kr.co.oneclass.common;
	
	import java.sql.Date;
	import java.util.List;

import kr.co.oneclass.classDetail.MaterialDTO;
import lombok.Getter;
	import lombok.Setter;
	import lombok.ToString;
	
	@Getter
	@Setter
	@ToString
	public class ClassDTO {
		private int classCode;
		private long operatorCode;
		private int categoryCode;
		private String categoryName;
		private String name;
		private int price;
		private int desiredPrice;
		private String zipcode;
		private String address;
		private String oldAddress;
		private String address2;
		private String ingredientsInclude;
		private String status;
		private int reviewSum;
		private double starRatingAvg;
		private int viewsCnt;
		private String singleIntroduce;
		private String introduce;
		private String finishIntroduce;
		private String finishImg;
		private double lat;
		private double lng;
		private String approvalStatus;
		private String approvalMemo;
		private Date writeDate;
		private List<ClassImageDTO> imageList;
		private List<TagDTO> tagList; 
		private String registerStep;
		private List<MaterialDTO> materialList;
	}
