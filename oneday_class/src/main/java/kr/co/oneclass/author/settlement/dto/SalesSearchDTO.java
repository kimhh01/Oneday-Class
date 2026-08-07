package kr.co.oneclass.author.settlement.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SalesSearchDTO {

    private long authorCode;        // 현재 로그인한 작가 코드
    private Date startDate;        // 조회 시작일
    private Date endDate;          // 조회 종료일
    private String paymentStatus;  // 결제 상태 필터
    private String searchType;     // 검색 대상 구분
    private String keyword;        // 검색어
    private int page;              // 현재 페이지 번호
    private int startRow;          // 조회 시작 행 번호
    private int endRow;            // 조회 종료 행 번호
}
