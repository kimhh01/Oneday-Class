package kr.co.oneclass.search;

import kr.co.oneclass.main.ClassDTO;
import kr.co.oneclass.main.ClassImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchDAO sDAO;

    /**
     * 통합검색 작가 프리뷰
     */
    public List<CreatorDTO> searchCreatorPreview(String keyword) {
        List<CreatorDTO> creatorList = sDAO.selectCreatorPreview(keyword);
        bindClassListToCreator(creatorList);
        return creatorList;
    }

    /**
     * 통합검색 클래스 프리뷰
     */
    public List<ClassDTO> searchClassPreview(String keyword) {
        return sDAO.selectClassPreview(keyword);
    }

    /**
     * 작가 더보기 페이지
     */
    public List<CreatorDTO> searchCreatorList(String keyword, int startNum, int endNum) {
        List<CreatorDTO> creatorList = sDAO.selectCreatorList(keyword, startNum, endNum);
        bindClassListToCreator(creatorList); 
        return creatorList;
    }

    /**
     * 클래스 더보기 페이지
     */
    public List<ClassDTO> searchClassList(String keyword, int startNum, int endNum) {
        return sDAO.selectClassList(keyword, startNum, endNum);
    }

    /**
     * 작가별 클래스 목록 조회
     */
    public List<ClassDTO> searchClassByCreator(long operatorCode) {
        return sDAO.selectClassByCreator(operatorCode);
    }

    /**
     * 검색어에 해당하는 클래스 전체 개수 조회
     */
    public int searchClassCount(String keyword) {
        return sDAO.selectClassCount(keyword);
    }

    /**
     * 검색어에 해당하는 작가 전체 명수 조회
     */
    public int searchCreatorCount(String keyword) {
        return sDAO.selectCreatorCount(keyword);
    }

    /**
     * 클래스 이미지 목록 조회
     */
    public List<ClassImageDTO> searchImage(long classCode) {
        return sDAO.selectImage(classCode);
    }
    
    private void bindClassListToCreator(List<CreatorDTO> creatorList) {
        if (creatorList != null && !creatorList.isEmpty()) {
            for (CreatorDTO creator : creatorList) {
                // 1. 작가의 클래스 목록 조회
                List<ClassDTO> classList = sDAO.selectClassByCreator(creator.getOperatorCode());
                
                if (classList != null && !classList.isEmpty()) {
                    for (ClassDTO cls : classList) {
                        // 2. 클래스 대표 이미지 조회 및 세팅
                        List<ClassImageDTO> imgList = sDAO.selectImage(cls.getClassCode());
                        if (imgList != null && !imgList.isEmpty()) {
                            // 대표 이미지가 설정되어 있다면 mainImage 변수에 세팅
                            cls.setMainImage(imgList.get(0).getImage());
                        }
                    }
                }
                // 3. 작가 DTO에 클래스 리스트 전달
                creator.setClassList(classList);
            }
        }
    }
    
}