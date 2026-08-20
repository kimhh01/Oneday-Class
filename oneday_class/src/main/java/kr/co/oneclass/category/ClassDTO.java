package kr.co.oneclass.category;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class ClassDTO {
    private long classCode;
    private long operatorCode;
    private long categoryCode;
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
    private int starRatingAvg;
    private int viewsCnt;
    private String singleIntroduce;
    private String introduce;
    private String finishIntroduce;
    private double lat;
    private double lng;
    private String approvalStatus;
    private String approvalMemo;
    private Date writeDate;
    private String registerStep;
    
    private String mainImage;
		
}