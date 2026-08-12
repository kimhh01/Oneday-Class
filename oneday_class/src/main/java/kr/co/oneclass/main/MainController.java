package kr.co.oneclass.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 1. 메인 페이지 연결 
    @GetMapping("/main")
    public String mainPage() {
        return "main/main"; 
    }

    // 2. 검색 페이지 연결 
    @GetMapping("/search")
    public String searchPage() {
        return "search/search_keyword"; 
    }
    
    // 3. 카테고리 페이지 연결 
    @GetMapping("/category")
    public String categoryPage() {
        return "category/category_all"; 
    }
 // 인기뷰티 연결 (주소: /top_category/beauty)
    @GetMapping("/top_category/beauty")
    public String beautyPage() {
        return "top_category/popular_beauty";
    }

    // 인기베이킹 연결 (주소: /top_category/baking)
    @GetMapping("/top_category/baking")
    public String bakingPage() {
        return "top_category/popular_baking";
    }

    // 인기액티비티 연결 (주소: /top_category/activity)
    @GetMapping("/top_category/activity")
    public String activityPage() {
        return "top_category/popular_activity";
    }
}