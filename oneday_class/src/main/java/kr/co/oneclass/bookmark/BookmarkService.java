package kr.co.oneclass.bookmark;

import kr.co.oneclass.board.RangeDTO;

import java.util.List;

public interface BookmarkService {

    int totalCnt(String memberCode, RangeDTO rDTO);

    int pageScale();

    int totalPage(int totalCnt, int pageScale);

    int startNum(int nowPage, int pageScale);

    int endNum(int nowPage, int pageScale);

    List<Bookmark> getBookmarkList(int memberCode, RangeDTO rDTO);

    boolean toggleBookmark(int memberCode, int classCode);
}