package kr.co.oneclass.author.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewSearchDTO {

    private long authorCode;      // 현재 로그인한 작가 코드
    private String replyStatus;  // 전체, 답글 완료, 답글 미작성 필터
    private int classCode;       // 클래스별 필터이며 0이면 전체 클래스
    private String sortType;     // 최신순, 오래된순, 별점 높은순, 별점 낮은순
    private int page;            // 현재 페이지 번호
    private int startRow;        // 조회 시작 행 번호
    private int endRow;          // 조회 종료 행 번호
}
