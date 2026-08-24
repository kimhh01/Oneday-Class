package kr.co.oneclass.search;

import kr.co.oneclass.main.ClassDTO;
import kr.co.oneclass.main.ClassImageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchDAO {

    // 통합검색 작가 프리뷰
    List<CreatorDTO> selectCreatorPreview(String keyword);

    // 통합검색 클래스 프리뷰
    List<ClassDTO> selectClassPreview(String keyword);

    // 작가 더보기 페이지 (페이징)
    List<CreatorDTO> selectCreatorList(@Param("keyword") String keyword, 
                                       @Param("startNum") int startNum, 
                                       @Param("endNum") int endNum);

    // 클래스 더보기 페이지 (페이징)
    List<ClassDTO> selectClassList(@Param("keyword") String keyword, 
                                   @Param("startNum") int startNum, 
                                   @Param("endNum") int endNum);

    // 작가별 클래스 목록 조회
    List<ClassDTO> selectClassByCreator(long operatorCode);

    // 검색어에 해당하는 클래스 전체 개수 조회
    int selectClassCount(String keyword);

    // 검색어에 해당하는 작가 전체 명수 조회
    int selectCreatorCount(String keyword);

    // 클래스 이미지 목록 조회
    List<ClassImageDTO> selectImage(long classCode);
    
 
}