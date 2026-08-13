package kr.co.oneclass.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDTO {
    private int categoryCode;
    private Integer parentCategoryCode; // 👈 int -> Integer 로 수정 (null 보존 목적)
    private String categoryName;
}