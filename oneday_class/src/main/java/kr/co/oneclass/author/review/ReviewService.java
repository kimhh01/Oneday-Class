package kr.co.oneclass.author.review;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.oneclass.common.AESUtil;

@Service
public class ReviewService {

    private final ReviewDAO rDAO;

    public ReviewService(ReviewDAO rDAO) {
        this.rDAO = rDAO;
    }

    // 해당 작가 클래스에 작성된 전체 리뷰, 답글 완료, 답글 미작성 개수를 조회한다
    public ReviewSummaryDTO getReviewSummary(long authorCode) {
        ReviewSummaryDTO summary = rDAO.selectReviewSummary(authorCode);
        // 템플릿이 요약 지표를 바로 참조하므로 리뷰가 없어도 빈 객체를 돌려준다
        return summary == null ? new ReviewSummaryDTO() : summary;
    }

    // 리뷰 클래스별 필터에 사용할 작가의 클래스 목록을 조회한다
    public List<ReviewClassDTO> getReviewClassList(long authorCode) {
        return rDAO.selectReviewClassList(authorCode);
    }

    // 답글 상태, 클래스, 정렬 조건과 페이지 번호에 맞는 리뷰 목록을 조회한다
    public List<ReviewListDTO> getReviewList(ReviewSearchDTO searchDTO) {
        List<ReviewListDTO> reviews = rDAO.selectReviewList(searchDTO);
        reviews.forEach(review ->
                review.setMemberName(AESUtil.decrypt(review.getMemberName())));
        return reviews;
    }

    // 현재 필터 조건에 해당하는 전체 리뷰 수를 조회하여 페이지 계산에 사용한다
    public int getReviewCount(ReviewSearchDTO searchDTO) {
        return rDAO.selectReviewCount(searchDTO);
    }

    // 현재 작가의 클래스에 작성된 리뷰인지 확인하고 상세정보를 조회한다
    // Mapper 의 OPERATOR_CODE 조건이 소유자 검증을 겸하므로 남의 리뷰는 null 이 된다
    public ReviewDetailDTO getReviewDetail(long authorCode, int reviewCode) {
        ReviewDetailDTO review = rDAO.selectReviewDetail(authorCode, reviewCode);
        if (review == null) {
            return null;
        }
        review.setMemberName(AESUtil.decrypt(review.getMemberName()));
        review.setImageList(rDAO.selectReviewImageList(reviewCode));
        return review;
    }

    // 리뷰 소유 관계와 기존 답글 여부를 확인한 후 새 답글을 등록한다
    // 두 검증 모두 Mapper 의 WHERE 조건이 담당한다 - 조건에 안 맞으면 변경 행수가 0 이다
    public boolean addReviewReply(ReviewReplyDTO replyDTO) {
        normalizeReplyContent(replyDTO);
        return rDAO.insertReviewReply(replyDTO) == 1;
    }

    // 현재 작가가 작성한 답글인지 확인한 후 답글 내용을 수정한다
    public boolean modifyReviewReply(ReviewReplyDTO replyDTO) {
        normalizeReplyContent(replyDTO);
        return rDAO.updateReviewReply(replyDTO) == 1;
    }

    // 현재 작가가 작성한 답글인지 확인한 후 답글을 삭제한다
    // 리뷰 원문은 남기고 REPLY_CONTENT / REPLY_DATE 만 비운다
    public boolean removeReviewReply(long authorCode, int reviewCode) {
        return rDAO.deleteReviewReply(authorCode, reviewCode) == 1;
    }

    private void normalizeReplyContent(ReviewReplyDTO replyDTO) {
        String content = replyDTO == null ? null : replyDTO.getReplyContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("답글 내용을 입력해주세요.");
        }

        content = content.trim();
        if (content.length() > 255) {
            throw new IllegalArgumentException("답글은 255자 이내로 입력해주세요.");
        }
        replyDTO.setReplyContent(content);
    }

    // 리뷰 원문(별점·내용)은 수강생의 콘텐츠이므로 작가가 숨기거나 삭제하는 기능은 두지 않는다.
    // 작가가 건드릴 수 있는 것은 자기 답글뿐이다 - 위 세 메서드가 전부다.
}
