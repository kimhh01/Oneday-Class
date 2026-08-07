package kr.co.oneclass.author.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorSummaryDTO {

    private long authorCode;           // 작가 코드
    private String authorName;        // 작가명
    private String profileImagePath;  // 작가 프로필 이미지 경로
    private int classCount;           // 운영 클래스 수
    private double averageRating;     // 평균 별점
    private int reviewCount;          // 전체 리뷰 수
}
