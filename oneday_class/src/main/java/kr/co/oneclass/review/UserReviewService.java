package kr.co.oneclass.review;

public interface UserReviewService {

    // 리뷰 등록 (ReviewDTO 내부의 images 필드 활용)
    boolean writeReview(ReviewDTO rdto);

    // 리뷰 상세 조회
    Review getReview(int reservationCode);
}