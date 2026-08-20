package kr.co.oneclass.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDTO {

    private int noticeCode;      // 공지사항 번호
    private String title;        // 제목
    private String content;      // 내용
    private String noticeType;   // 공지 구분 (서비스, 이용 안내 등)
    private String noticeDate;   // 등록일 (MyBatis TO_CHAR formatting 대응)
    
    private Integer prevNoticeCode; // 이전 글 번호 (없으면 null)
    private Integer nextNoticeCode; // 다음 글 번호 (없으면 null)

    // Thymeleaf 템플릿(notice.html) 내 category 필드 호환용 Getter
    public String getCategory() {
        return this.noticeType;
    }
}