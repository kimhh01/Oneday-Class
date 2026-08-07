package kr.co.oneclass.author.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewClassDTO {

    private int classCode;      // 클래스 코드
    private String classTitle;  // 필터에 표시할 클래스명
    private int reviewCount;    // 해당 클래스에 작성된 리뷰 개수
}
