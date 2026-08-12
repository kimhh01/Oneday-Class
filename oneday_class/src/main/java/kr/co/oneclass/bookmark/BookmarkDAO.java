package kr.co.oneclass.bookmark;

import kr.co.oneclass.board.RangeDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookmarkDAO {

    /**
     * 1. 관심 클래스 전체 개수 조회 (CATEGORY 조인 적용)
     */
    int selectTotalCnt(@Param("memberCode") String memberCode, @Param("rDTO") RangeDTO rDTO);

    /**
     * 2. 관심 클래스 목록 조회 (ROWNUM 페이징 + CLASS_IMG 조인 + CATEGORY 조인 + 주소 정제)
     */
    List<Bookmark> selectBookmark(@Param("memberCode") String memberCode, @Param("rDTO") RangeDTO rDTO);

    /**
     * 3. 관심 클래스 추가
     */
   
    int insertBookmark(@Param("memberCode") String memberCode, @Param("classCode") String classCode);

    /**
     * 4. 관심 클래스 삭제
     */
    
    int deleteBookmark(@Param("memberCode") String memberCode, @Param("classCode") String classCode);
    
    
    
    /**
     * 5. 관심 존재 여부 확인
     */
    
    int checkBookmark(@Param("memberCode") String memberCode, @Param("classCode") String classCode);
}