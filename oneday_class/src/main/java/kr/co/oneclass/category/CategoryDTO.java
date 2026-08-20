package kr.co.oneclass.category;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryDTO {
    private long categoryCode;
    private Long parentCategoryCode; 
    private String categoryName;  
}