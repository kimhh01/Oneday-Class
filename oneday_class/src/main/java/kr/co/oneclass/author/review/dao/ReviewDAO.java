package kr.co.oneclass.author.review.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.review.dto.ReviewClassDTO;
import kr.co.oneclass.author.review.dto.ReviewDetailDTO;
import kr.co.oneclass.author.review.dto.ReviewImageDTO;
import kr.co.oneclass.author.review.dto.ReviewListDTO;
import kr.co.oneclass.author.review.dto.ReviewReplyDTO;
import kr.co.oneclass.author.review.dto.ReviewSearchDTO;
import kr.co.oneclass.author.review.dto.ReviewSummaryDTO;

@Repository
public class ReviewDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.review.dao.ReviewDAO.";

    private final SqlSessionTemplate sqlSession;

    public ReviewDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 작가별 전체 리뷰 수, 답글 완료 수, 답글 미작성 수를 조회한다
    public ReviewSummaryDTO selectReviewSummary(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectReviewSummary", authorCode);
    }

    // 클래스별 리뷰 필터에 사용할 작가의 클래스 목록을 조회한다
    public List<ReviewClassDTO> selectReviewClassList(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectReviewClassList", authorCode);
    }

    // 필터, 정렬, 페이지 조건에 해당하는 리뷰 목록을 조회한다
    public List<ReviewListDTO> selectReviewList(ReviewSearchDTO searchDTO) {
        return sqlSession.selectList(NAMESPACE + "selectReviewList", searchDTO);
    }

    // 필터 조건에 해당하는 전체 리뷰 수를 조회한다
    public int selectReviewCount(ReviewSearchDTO searchDTO) {
        return sqlSession.selectOne(NAMESPACE + "selectReviewCount", searchDTO);
    }

    // 작가 코드와 리뷰 코드에 해당하는 리뷰 상세정보를 조회한다
    public ReviewDetailDTO selectReviewDetail(long authorCode, int reviewCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("reviewCode", reviewCode);
        return sqlSession.selectOne(NAMESPACE + "selectReviewDetail", param);
    }

    // 리뷰에 첨부된 이미지 목록을 이미지 순서대로 조회한다
    public List<ReviewImageDTO> selectReviewImageList(int reviewCode) {
        return sqlSession.selectList(NAMESPACE + "selectReviewImageList", reviewCode);
    }

    // 리뷰에 새로운 작가 답글을 등록한다
    // 답글 전용 테이블이 없어 REVIEW.REPLY_CONTENT 를 채우는 UPDATE 다
    public int insertReviewReply(ReviewReplyDTO replyDTO) {
        return sqlSession.update(NAMESPACE + "insertReviewReply", replyDTO);
    }

    // 등록된 작가 답글의 내용과 수정일을 변경한다
    public int updateReviewReply(ReviewReplyDTO replyDTO) {
        return sqlSession.update(NAMESPACE + "updateReviewReply", replyDTO);
    }

    // 해당 리뷰에 등록된 작가 답글을 삭제한다
    public int deleteReviewReply(long authorCode, int reviewCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("reviewCode", reviewCode);
        return sqlSession.update(NAMESPACE + "deleteReviewReply", param);
    }

    // 리뷰 원문을 변경하는 메서드는 두지 않는다.
    // 다이어그램의 updateReviewStatus(리뷰 숨김·삭제)는 정책상 제외했다 - 3절 참고.
}
