package kr.co.oneclass.category;

import kr.co.oneclass.bookmark.BookmarkService;
import kr.co.oneclass.main.ClassDTO;
import kr.co.oneclass.member.Member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookmarkService bookmarkService;
    
    // 세션 및 북마크 공통 처리 헬퍼 메서드
    private void setSessionAndBookmark(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
            List<Long> bookmarkedClassCodes = bookmarkService.getBookmarkClassCodes(loginMember.getMemberCode());
            model.addAttribute("bookmarkedClassCodes", bookmarkedClassCodes);
        }
    }

    /**
     * 카테고리 메인 (전체 목록)
     */
    @GetMapping("/category")
    public String categoryForm(HttpSession session, Model model) {
        CategorySearchDTO searchDTO = new CategorySearchDTO();
        searchDTO.setSort("신규순");
        return categoryClass(session, searchDTO, model);
    }

    /**
     * 선택한 카테고리·정렬·필터 조건의 클래스 목록
     */
    @GetMapping("/category/class")
    public String categoryClass(HttpSession session, CategorySearchDTO searchDTO, Model model) {
        // ★ 세션 및 북마크 정보 모델 추가
        setSessionAndBookmark(session, model);

        List<CategoryDTO> categoryList = categoryService.searchCategoryList();
        List<ClassDTO> classList = categoryService.searchCategoryClass(searchDTO);

        String currentCategoryName = "전체";
        
        if (searchDTO.isSameDayOnly()) {
            currentCategoryName = "⚡ 오늘 당일예약";
        } 
        if (searchDTO.isSameWeekOnly()) {
            currentCategoryName = "주말예약";
        }

        // 현재 선택된 카테고리 이름 찾기
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

        return "category/categoryClass";
    }
    
    @GetMapping("/category/today")
    public String sameToday(HttpSession session, CategorySearchDTO searchDTO, Model model) {
        // ★ 세션 및 북마크 정보 모델 추가
        setSessionAndBookmark(session, model);

        searchDTO.setSameDayOnly(true);
        
        List<CategoryDTO> categoryList = categoryService.searchCategoryList();
        List<ClassDTO> classList = categoryService.searchCategoryClass(searchDTO);
        
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("classList", classList);
        model.addAttribute("searchDTO", searchDTO);
        model.addAttribute("currentCategoryName", "⚡ 오늘 당일예약");
        
        return "category/categoryClass";
    }
    
    @GetMapping("/category/week")
    public String sameWeek(HttpSession session, CategorySearchDTO searchDTO, Model model) {
        // ★ 세션 및 북마크 정보 모델 추가
        setSessionAndBookmark(session, model);

        searchDTO.setSameWeekOnly(true);
        List<CategoryDTO> categoryList = categoryService.searchCategoryList();
        List<ClassDTO> classList = categoryService.searchCategoryClass(searchDTO);
          
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("classList", classList);
        model.addAttribute("searchDTO", searchDTO);
        model.addAttribute("currentCategoryName", "주말예약");
    	
        return "category/categoryClass";
    }
}