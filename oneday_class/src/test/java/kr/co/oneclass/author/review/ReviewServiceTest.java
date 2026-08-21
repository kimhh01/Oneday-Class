package kr.co.oneclass.author.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


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
}
