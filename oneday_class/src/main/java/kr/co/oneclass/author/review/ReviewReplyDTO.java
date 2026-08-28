package kr.co.oneclass.author.review;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewReplyDTO {

    private int reviewCode;       // 답글을 작성할 리뷰 코드
    private long authorCode;       // 답글을 작성한 작가 코드
    private String replyContent;  // 작가 답글 내용
    private Date replyDate;       // 답글 작성일 (저장 시 SYSDATE로 생성)
}
