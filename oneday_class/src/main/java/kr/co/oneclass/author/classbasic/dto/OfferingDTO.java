package kr.co.oneclass.author.classbasic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 클래스 등록 상세정보 단계의 제공 항목 선택 목록용 DTO. 클래스 다이어그램에는 없고 화면 요구로 추가했다. */
@Getter
@Setter
@NoArgsConstructor
public class OfferingDTO {

    private int offeringCode;     // 제공 항목 코드
    private String offeringName;  // 제공 항목명 (주차, 와이파이 등)
}
