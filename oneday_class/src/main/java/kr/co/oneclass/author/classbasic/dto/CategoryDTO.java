package kr.co.oneclass.author.classbasic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 클래스 등록 기본정보 단계의 카테고리 선택 목록용 DTO. 클래스 다이어그램에는 없고 화면 요구로 추가했다. */
@Getter
@Setter
@NoArgsConstructor
public class CategoryDTO {

    private int categoryCode;  // 카테고리 코드
    private String name;       // 카테고리명
}
