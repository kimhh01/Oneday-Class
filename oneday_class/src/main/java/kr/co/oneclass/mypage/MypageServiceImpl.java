package kr.co.oneclass.mypage;

import kr.co.oneclass.mypage.MypageDAO;
import kr.co.oneclass.notice.NoticeDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MypageServiceImpl implements MypageService {

    @Autowired
    private MypageDAO mypageDAO;

    @Override
    public NoticeDTO getRecentNotice() {
        return mypageDAO.selectRecentNotice();
    }
    
 // 💡 추가: 로그인 회원의 작가 등록/승인 여부 확인
    public boolean isAuthor(int memberCode) {
        return mypageDAO.selectAuthorCount(memberCode) > 0;
    }
}