package kr.co.oneclass.category;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategorySearchDTO {
    private long categoryCode;       // 대분류 카테고리 코드
    private long subCategoryCode;    // 소분류 카테고리 코드
    private String sort;             // 정렬 기준 (신규순, 추천순, 낮은 가격순, 높은 가격순)
    private int minPrice;            // 최소 가격
    private int maxPrice;            // 최대 가격
    private int minPeople;           // 최소 수강 인원
	
	
}