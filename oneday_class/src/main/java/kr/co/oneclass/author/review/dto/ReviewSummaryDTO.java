package kr.co.oneclass.author.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewSummaryDTO {

    private int totalReviewCount;       // 전체 리뷰 개수
    private int repliedReviewCount;     // 작가 답글이 등록된 리뷰 개수
    private int unansweredReviewCount;  // 아직 작가 답글이 없는 리뷰 개수
}
