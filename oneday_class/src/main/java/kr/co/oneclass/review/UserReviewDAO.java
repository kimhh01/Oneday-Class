package kr.co.oneclass.review;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserReviewDAO {

    // 1. 리뷰 본문 저장 (XML에서 selectKey로 생성된 reviewCode 반환)
    int insertReview(ReviewDTO rdto);

    // 2. 리뷰 이미지 단건 저장 (반복문에서 호출)
    int insertReviewImg(@Param("reviewCode") int reviewCode, @Param("imagePath") String imagePath);

    // 3. 리뷰 상세 조회
    Review selectReview(@Param("classCode") int classCode, @Param("memberCode") int memberCode);
}