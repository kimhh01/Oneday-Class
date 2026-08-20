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

    public List<CategoryDTO> searchCategoryList() {
        return cDAO.selectCategoryList();
    }

    public List<ClassDTO> searchCategoryClass(CategorySearchDTO searchDTO) {
        return cDAO.selectCategoryClass(searchDTO);
    }

    public List<ClassImageDTO> searchImage(long classCode) {
        return cDAO.selectImage(classCode);
    }
}