package kr.co.oneclass.author.review.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDetailDTO {

    private int reviewCode;                                      // 리뷰 코드
    private int classCode;                                       // 리뷰가 작성된 클래스 코드
    private String classTitle;                                   // 클래스명
    private String memberName;                                   // 리뷰 작성자명
    private String memberProfilePath;                            // 리뷰 작성자 프로필 이미지 경로
    private double rating;                                       // 리뷰 별점
    private String reviewContent;                                // 리뷰 전체 내용
    private Date reviewDate;                                     // 리뷰 작성일
    private String replyStatus;                                  // 답글 등록 여부
    private String replyContent;                                 // 작가 답글 내용
    private Date replyDate;                                      // 작가 답글 작성일
    // reviewStatus 없음 - 리뷰 공개·숨김 상태는 작가가 제어하지 않는다

    private List<ReviewImageDTO> imageList = new ArrayList<>();  // 리뷰에 첨부된 이미지 목록
}
