package kr.co.oneclass.main;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	@Autowired
    private MainService ms;

    // 1. 메인 페이지 (인기 클래스 + 당일/주말 및 카테고리별 데이터)
    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("todayClasses", ms.searchTodayClass());
        model.addAttribute("weekendClasses", ms.searchWeekendClass());

        // 1. 이달의 인기 클래스 상위 3개
        model.addAttribute("topRatedList", ms.searchTopRatedClass(0, 3)); 

        // 2. 인기 디저트/베이킹 클래스 상위 6개
        model.addAttribute("bakingList", ms.searchTopRatedClass(0, 5)); 

        // 3. 인기 뷰티 클래스 상위 3개
        model.addAttribute("beautyList", ms.searchTopRatedClass(0, 3)); 

        return "main/main"; 
    }

	/*
	 * // 2-1. 통합 검색 페이지
	 * 
	 * @GetMapping("/search") public String searchPage(@RequestParam(value =
	 * "keyword", required = false) String keyword, Model model) {
	 * model.addAttribute("keyword", keyword); return "search/search_keyword"; }
	 * 
	 * // 2-2. 검색 결과 더보기 페이지
	 * 
	 * @GetMapping("/search/more") public String searchPageMore(@RequestParam(value
	 * = "keyword", required = false) String keyword, Model model) {
	 * model.addAttribute("keyword", keyword); return "search/search_keyword_more";
	 * }
	 */
    
//    // 3. 카테고리 페이지 (SearchFilterDTO 없이 개별 @RequestParam으로 처리)
//    @GetMapping("/category")
//    public String categoryPage(
//            @RequestParam(value = "categoryCode", required = false) Integer categoryCode,
//            @RequestParam(value = "minPrice", required = false) Integer minPrice,
//            @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
//            @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
//            Model model) {
//        
//        // DTO 없이 파라미터들을 받아 필요한 데이터 넘김
//        model.addAttribute("categoryCode", categoryCode);
//        model.addAttribute("minPrice", minPrice);
//        model.addAttribute("maxPrice", maxPrice);
//        model.addAttribute("sort", sort);
//
//        return "category/category_all"; 
//    }

    // 4-1. 인기 뷰티 랭킹 페이지
    @GetMapping("/top_category/beauty")
    public String beautyPage(@RequestParam(value = "categoryCode", defaultValue = "1") int categoryCode, Model model) {
        List<ClassDTO> beautyList = ms.searchTopRatedClassList(categoryCode);
        model.addAttribute("classList", beautyList);
        return "top_category/popular_beauty";
    }

    // 4-2. 인기 베이킹 랭킹 페이지
    @GetMapping("/top_category/baking")
    public String bakingPage(@RequestParam(value = "categoryCode", defaultValue = "2") int categoryCode, Model model) {
        List<ClassDTO> bakingList = ms.searchTopRatedClassList(categoryCode);
        model.addAttribute("classList", bakingList);
        return "top_category/popular_baking";
    }

    // 4-3. 인기 액티비티 랭킹 페이지
    @GetMapping("/top_category/activity")
    public String activityPage(@RequestParam(value = "categoryCode", defaultValue = "3") int categoryCode, Model model) {
        List<ClassDTO> activityList = ms.searchTopRatedClassList(categoryCode);
        model.addAttribute("classList", activityList);
        return "top_category/popular_activity";
    }

 // 5. 조건별 예약 가능 클래스 검색
    @GetMapping("/main/search-available")
    public String searchAvailableClass(@RequestParam(value = "categoryCode", defaultValue = "0") int categoryCode,
                                       ScheduleDTO scheduleDTO,
                                       Model model) {
        List<ClassDTO> availableList = ms.searchAvailableClass(categoryCode, scheduleDTO);
        model.addAttribute("availableList", availableList);
        return "main/available_list";
    }
    
    //8080->main 화면
    @GetMapping("/")
    public String index() {
        return "redirect:/main"; // http://localhost:8080 접속 시 /main으로 이동
    }
}