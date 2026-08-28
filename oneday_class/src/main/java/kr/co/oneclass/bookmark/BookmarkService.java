package kr.co.oneclass.bookmark;

import kr.co.oneclass.board.RangeDTO;
import kr.co.oneclass.common.CategoryDTO;

import java.util.List;

public interface BookmarkService {

    int totalCnt(String memberCode, RangeDTO rDTO);

    int pageScale();

    int totalPage(int totalCnt, int pageScale);

    int startNum(int nowPage, int pageScale);

    int endNum(int nowPage, int pageScale);

    List<Bookmark> getBookmarkList(int memberCode, RangeDTO rDTO);

    boolean toggleBookmark(int memberCode, int classCode);

	/**
	 * 로그인 회원이 찜한 클래스들의 부모 카테고리 목록 조회
	 */
    
	List<CategoryDTO> getBookmarkCategories(int memberCode);
	
	//추 가: 회원이 찜한 클래스 코드 목록 조회
	List<Long> getBookmarkClassCodes(int memberCode);
}