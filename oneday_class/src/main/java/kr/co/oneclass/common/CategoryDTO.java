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
	private int parentCategoryCode;
	private String categoryName;
}
