package kr.co.oneclass.board;

import org.springframework.stereotype.Component;

@Component
public class BoardUtil {

    // 한 페이지에 출력할 게시글/클래스 수
    public static final int PAGE_SCALE = 6;
    // 하단 네비게이션에 한번에 표시할 페이지 번호 개수
    public static final int PAGE_GROUP = 5;

    /**
     * 페이지네이션 HTML 태그 생성 메서드
     *
     * @param totalCnt 전체 게시글(데이터) 수
     * @param nowPage  현재 페이지 번호
     * @param query    추가 검색 쿼리 스트링 (예: "&filed=className&keyword=도자기")
     * @return HTML 렌더링용 페이지네이션 문자열
     */
    public static String pagination(int totalCnt, int nowPage, String query) {
        // 데이터가 없는 경우 빈 값 반환
        if (totalCnt <= 0) {
            return "";
        }

        // 1. 전체 페이지 수 계산
        int totalPage = (int) Math.ceil((double) totalCnt / PAGE_SCALE);
        if (nowPage < 1) nowPage = 1;
        if (nowPage > totalPage) nowPage = totalPage;

        // 2. 현재 페이지 그룹의 시작/끝 페이지 번호 계산
        int startPage = ((nowPage - 1) / PAGE_GROUP) * PAGE_GROUP + 1;
        int endPage = startPage + PAGE_GROUP - 1;
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        // 3. 쿼리 스트링 포맷 정리 (& 구문 처리)
        if (query == null) {
            query = "";
        }
        if (!query.isEmpty() && !query.startsWith("&") && !query.startsWith("?")) {
            query = "&" + query;
        }

        // 4. HTML 생성
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"pagination-wrapper\">");

        // [이전] 버튼
        if (startPage > 1) {
            int prevPage = startPage - 1;
            sb.append(String.format("<a href=\"?nowPage=%d%s\" class=\"page-btn prev\">&laquo; 이전</a>", prevPage, query));
        } else {
            sb.append("<span class=\"page-btn prev disabled\">&laquo; 이전</span>");
        }

        // [페이지 번호 목록]
        for (int i = startPage; i <= endPage; i++) {
            if (i == nowPage) {
                sb.append(String.format("<span class=\"page-number active\">%d</span>", i));
            } else {
                sb.append(String.format("<a href=\"?nowPage=%d%s\" class=\"page-number\">%d</a>", i, query, i));
            }
        }

        // [다음] 버튼
        if (endPage < totalPage) {
            int nextPage = endPage + 1;
            sb.append(String.format("<a href=\"?nowPage=%d%s\" class=\"page-btn next\">다음 &raquo;</a>", nextPage, query));
        } else {
            sb.append("<span class=\"page-span next disabled\">다음 &raquo;</span>");
        }

        sb.append("</div>");

        return sb.toString();
    }
}