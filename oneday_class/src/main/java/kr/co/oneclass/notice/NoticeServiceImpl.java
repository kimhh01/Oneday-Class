package kr.co.oneclass.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeDAO nd;

    @Override
    public int totalCnt(Map<String, Object> paramMap) {
        return nd.selectTotalCnt(paramMap);
    }

    @Override
    public int pageScale() {
        return 10;
    }

    @Override
    public int totalPage(int totalCnt, int pageScale) {
        if (totalCnt == 0) return 1;
        return (int) Math.ceil((double) totalCnt / pageScale);
    }

    @Override
    public int startNum(int nowPage, int pageScale) {
        return (nowPage - 1) * pageScale + 1;
    }

    @Override
    public int endNum(int nowPage, int pageScale) {
        return nowPage * pageScale;
    }

    @Override
    public List<NoticeDTO> getNoticeList(Map<String, Object> paramMap) {
        return nd.selectList(paramMap);
    }

    @Override
    public NoticeDTO getNoticeDetail(String noticeCode) {
        return nd.selectDetail(noticeCode);
    }
}