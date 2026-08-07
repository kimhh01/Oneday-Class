package kr.co.oneclass.bookmark;

import kr.co.oneclass.board.RangeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired
    private BookmarkDAO bd;

    @Override
    public int totalCnt(String memberCode, RangeDTO rDTO) {
        return bd.selectTotalCnt(memberCode, rDTO);
    }

    @Override
    public int pageScale() {
        return 6;
    }

    @Override
    public int totalPage(int totalCnt, int pageScale) {
        if (totalCnt <= 0) return 0;
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
    public List<Bookmark> getBookmarkList(int memberCode, RangeDTO rDTO) {
        return bd.selectBookmark(String.valueOf(memberCode), rDTO);
    }

    @Override
    public boolean toggleBookmark(int memberCode, int classCode) {
        String mCode = String.valueOf(memberCode);
        String cCode = String.valueOf(classCode);

        // 먼저 삭제 시도
        int deleteResult = bd.deleteBookmark(mCode, cCode);

        if (deleteResult > 0) {
            // 기존 데이터 존재 시 삭제 처리 (관심 클래스 해제)
            return false;
        } else {
            // 기존 데이터 없을 시 등록 처리 (관심 클래스 등록)
            bd.insertBookmark(mCode, cCode);
            return true;
        }
    }
}