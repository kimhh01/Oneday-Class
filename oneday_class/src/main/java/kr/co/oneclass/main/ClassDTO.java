package kr.co.oneclass.main;

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
    
    private int price;//가격
    private int desiredPrice;//희망가격 ->(희망가격/가격)x100으로 할인율표기
    

 // 정가(price) 대비 희망/할인가(desiredPrice)의 할인율 (%) 계산
    public int getDiscountRate() {
        // 정가(price)가 할인가(desiredPrice)보다 클 때만 계산 진행
        if (this.price > 0 && this.desiredPrice > 0 && this.price > this.desiredPrice) {
            return (int) Math.round((1.0 - ((double) this.desiredPrice / this.price)) * 100);
        }
        return 0;
    }
    
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
    
    private boolean isBookmarked;
    
		
}