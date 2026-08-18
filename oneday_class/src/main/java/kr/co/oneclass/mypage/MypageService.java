package kr.co.oneclass.mypage;

import kr.co.oneclass.notice.NoticeDTO;

public interface MypageService {
    NoticeDTO getRecentNotice();
    
 // 💡 추가: 작가 승인 여부 확인 메서드 선언
    boolean isAuthor(int memberCode);
}