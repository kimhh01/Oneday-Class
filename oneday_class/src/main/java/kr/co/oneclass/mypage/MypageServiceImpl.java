package kr.co.oneclass.mypage;

import kr.co.oneclass.mypage.MypageDAO;
import kr.co.oneclass.notice.NoticeDoamin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MypageServiceImpl implements MypageService {

    @Autowired
    private MypageDAO mypageDAO;

    @Override
    public NoticeDoamin getRecentNotice() {
        return mypageDAO.selectRecentNotice();
    }
}