package kr.co.oneclass.author.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewImageDTO {

    private int reviewImageCode;  // 리뷰 이미지 코드
    private int reviewCode;       // 이미지가 속한 리뷰 코드
    private String imagePath;     // 저장된 리뷰 이미지 경로
    private int imageOrder;       // 리뷰 이미지 출력 순서
}
