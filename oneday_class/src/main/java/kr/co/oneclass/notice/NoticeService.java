package kr.co.oneclass.notice;

import java.util.List;
import java.util.Map;

public interface NoticeService {

    /**
     * 공지사항 전체 건수 조회
     */
    int totalCnt(Map<String, Object> paramMap);

    /**
     * 한 페이지당 노출할 게시글 수 (10개)
     */
    int pageScale();

    /**
     * 전체 페이지 수 계산
     */
    int totalPage(int totalCnt, int pageScale);

    /**
     * 현재 페이지의 시작 ROWNUM 계산
     */
    int startNum(int nowPage, int pageScale);

    /**
     * 현재 페이지의 끝 ROWNUM 계산
     */
    int endNum(int nowPage, int pageScale);

    /**
     * 공지사항 목록 조회 (페이지네이션 및 카테고리 적용)
     */
    List<NoticeDTO> getNoticeList(Map<String, Object> paramMap);

    /**
     * 공지사항 상세 조회
     */
    NoticeDTO getNoticeDetail(String noticeCode);
}