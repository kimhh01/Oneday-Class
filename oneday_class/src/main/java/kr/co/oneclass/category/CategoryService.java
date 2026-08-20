package kr.co.oneclass.category;

import kr.co.oneclass.main.ClassDTO;
import kr.co.oneclass.main.ClassImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDAO cDAO;

    /**
     * 전체 카테고리 목록 조회
     */
    public List<CategoryDTO> searchCategoryList() {
        return cDAO.selectCategoryList();
    }

    /**
     * 카테고리·정렬·가격·인원 조건에 맞는 클래스 목록 조회
     */
    public List<ClassDTO> searchCategoryClass(CategorySearchDTO searchDTO) {
        return cDAO.selectCategoryClass(searchDTO);
    }

    /**
     * 클래스 이미지 목록 조회
     */
    public List<ClassImageDTO> searchImage(long classCode) {
        return cDAO.selectImage(classCode);
    }
}