package kr.co.oneclass.author.review.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewListDTO {

    private int reviewCode;        // 리뷰 코드
    private int classCode;         // 리뷰가 작성된 클래스 코드
    private String classTitle;     // 클래스명
    private String thumbnailPath;  // 클래스 대표 이미지 경로
    private String memberName;     // 리뷰 작성자명
    private double rating;         // 리뷰 별점
    private String reviewContent;  // 리뷰 내용
    private Date reviewDate;       // 리뷰 작성일
    private String replyStatus;    // 답글 완료 또는 답글 미작성 상태
    private String replyContent;   // 목록에 미리 표시할 작가 답글 내용
    private Date replyDate;        // 작가 답글 작성일
    // reviewStatus 없음 - 리뷰 공개·숨김 상태는 작가가 제어하지 않는다
}
