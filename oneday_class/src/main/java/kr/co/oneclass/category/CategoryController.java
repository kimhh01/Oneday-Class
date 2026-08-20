package kr.co.oneclass.category;

import kr.co.oneclass.main.ClassDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService; // 👈 변수명 첫 글자를 소문자 c로 변경

    /**
     * 카테고리 메인 (전체 목록)
     */
    @GetMapping("/category")
    public String categoryForm(Model model) {
        CategorySearchDTO searchDTO = new CategorySearchDTO();
        return categoryClass(searchDTO, model);
    }

    /**
     * 선택한 카테고리·정렬·필터 조건의 클래스 목록
     */
    @GetMapping("/category/class")
    public String categoryClass(CategorySearchDTO searchDTO, Model model) {
        // 소문자 변수명(categoryService)으로 호출
        List<CategoryDTO> categoryList = categoryService.searchCategoryList();
        List<ClassDTO> classList = categoryService.searchCategoryClass(searchDTO);

     // 현재 선택된 카테고리 이름 찾기
        String currentCategoryName = "전체";
        if (searchDTO.getCategoryCode() > 0) {
            for (CategoryDTO cat : categoryList) {
                if (cat.getCategoryCode() == searchDTO.getCategoryCode()) {
                    currentCategoryName = cat.getCategoryName();
                    break;
                }
            }
        }
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("classList", classList);
        model.addAttribute("searchDTO", searchDTO);
        model.addAttribute("currentCategoryName", currentCategoryName);

        return "category/category_class";
    }
}