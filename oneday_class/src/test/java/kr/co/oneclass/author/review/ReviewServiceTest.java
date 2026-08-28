package kr.co.oneclass.author.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.oneclass.common.AESUtil;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewDAO reviewDAO;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewDAO);
    }

    @Test
    void blankReplyIsRejectedBeforeDatabaseUpdate() {
        ReviewReplyDTO reply = new ReviewReplyDTO();
        reply.setReplyContent("   ");

        assertThrows(IllegalArgumentException.class,
                () -> reviewService.addReviewReply(reply));

        verify(reviewDAO, never()).insertReviewReply(any());
    }

    @Test
    void replyIsTrimmedBeforeDatabaseUpdate() {
        ReviewReplyDTO reply = new ReviewReplyDTO();
        reply.setReplyContent("  감사합니다.  ");
        when(reviewDAO.insertReviewReply(reply)).thenReturn(1);

        reviewService.addReviewReply(reply);

        assertEquals("감사합니다.", reply.getReplyContent());
        verify(reviewDAO).insertReviewReply(reply);
    }

    @Test
    void overlongReplyIsRejectedBeforeDatabaseUpdate() {
        ReviewReplyDTO reply = new ReviewReplyDTO();
        reply.setReplyContent("가".repeat(256));

        assertThrows(IllegalArgumentException.class,
                () -> reviewService.modifyReviewReply(reply));

        verify(reviewDAO, never()).updateReviewReply(any());
    }

    @Test
    void reviewerNamesAreDecryptedForListAndDetail() {
        ReviewListDTO listReview = new ReviewListDTO();
        listReview.setMemberName(AESUtil.encrypt("김수강"));
        ReviewDetailDTO detailReview = new ReviewDetailDTO();
        detailReview.setMemberName(AESUtil.encrypt("박수강"));
        ReviewSearchDTO search = new ReviewSearchDTO();

        when(reviewDAO.selectReviewList(search)).thenReturn(List.of(listReview));
        when(reviewDAO.selectReviewDetail(7L, 11)).thenReturn(detailReview);
        when(reviewDAO.selectReviewImageList(11)).thenReturn(List.of());

        assertEquals("김수강", reviewService.getReviewList(search).get(0).getMemberName());
        assertEquals("박수강", reviewService.getReviewDetail(7L, 11).getMemberName());
    }

    @Test
    void legacyPlaintextReviewerNameRemainsUnchanged() {
        ReviewListDTO review = new ReviewListDTO();
        review.setMemberName("기존회원");
        ReviewSearchDTO search = new ReviewSearchDTO();
        when(reviewDAO.selectReviewList(search)).thenReturn(List.of(review));

        assertEquals("기존회원", reviewService.getReviewList(search).get(0).getMemberName());
    }
}
