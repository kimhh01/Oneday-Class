package kr.co.oneclass.main;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.bookmark.BookmarkService;
import kr.co.oneclass.category.CategoryDTO;
import kr.co.oneclass.member.Member;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class MainController {
	
    @Autowired
    private MainService ms;

    @Autowired
    private BookmarkService bookmarkService;
    
 // 1. 메인 페이지 (인기 클래스 + 당일/주말 및 카테고리별 데이터 + 로그인 세션 처리)
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) { 

        // 로그인 회원 정보 세션 확인 및 북마크 목록 전달
        Member loginMember = (Member) session.getAttribute("loginMember"); 
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember); 
            
            List<Long> bookmarkedClassCodes = bookmarkService.getBookmarkClassCodes(loginMember.getMemberCode());
            model.addAttribute("bookmarkedClassCodes", bookmarkedClassCodes);
        } // ★ if 문 닫기 (지역 카운트 로직을 로그인과 완전 분리)

        // 1. 화면 표시 키워드 <-> 실제 DB 주소 검색 키워드 매핑
        Map<String, String> regionSearchMap = new HashMap<>();
        regionSearchMap.put("홍대", "마포");
        regionSearchMap.put("강남", "강남");
        regionSearchMap.put("성수", "성수");
        regionSearchMap.put("송파", "송파");
        regionSearchMap.put("마포", "마포");
        regionSearchMap.put("잠실", "송파");
        regionSearchMap.put("용산", "용산");
        regionSearchMap.put("부산", "부산");
        regionSearchMap.put("수원", "수원");
        regionSearchMap.put("광주", "광주");
        regionSearchMap.put("대전", "대전");

        Map<String, Integer> regionCounts = new HashMap<>();

        // 2. 매핑 데이터를 기반으로 DB 조회 (비로그인 유저도 실행)
        for (Map.Entry<String, String> entry : regionSearchMap.entrySet()) {
            String uiKeyword = entry.getKey();      // 화면에 보여줄 이름 (홍대, 잠실 등)
            String dbSearchWord = entry.getValue(); // DB 주소에서 찾을 단어 (마포, 송파 등)
            
            int count = ms.getRegionClassCount(dbSearchWord);
            regionCounts.put(uiKeyword, count);
        }

        model.addAttribute("regionCounts", regionCounts);

        // 전체 카테고리 목록 전달 (메인 검색바 드롭다운용)
        List<CategoryDTO> categoryList = ms.getCategoryList(); 
        model.addAttribute("categoryList", categoryList); 

        // 당일 / 주말 예약 가능 클래스 데이터
        model.addAttribute("todayClasses", ms.searchTodayClass()); 
        model.addAttribute("weekendClasses", ms.searchWeekendClass()); 

        // 1-1. 이달의 인기 클래스 상위 3개 (전체 카테고리 = 0)
        model.addAttribute("topRatedList", ms.searchTopRatedClass(0, 3)); 

        // 1-2. 인기 디저트/베이킹 클래스 상위 5개 (베이킹 카테고리 코드 = 2)
        model.addAttribute("bakingList", ms.searchTopRatedClass(2, 5)); 

        // 1-3. 인기 뷰티 클래스 상위 3개 (뷰티 카테고리 코드 = 5)
        model.addAttribute("beautyList", ms.searchTopRatedClass(5, 3)); 

        return "main/main"; 
    }

    // 2. 인기 뷰티 랭킹 페이지
    @GetMapping("/topCategory/beauty")
    public String beautyPage(HttpSession session, 
                             @RequestParam(value = "categoryCode", defaultValue = "5") int categoryCode, 
                             Model model) { 
        Member loginMember = (Member) session.getAttribute("loginMember"); 
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
            List<Long> bookmarkedClassCodes = bookmarkService.getBookmarkClassCodes(loginMember.getMemberCode());
            model.addAttribute("bookmarkedClassCodes", bookmarkedClassCodes);
        }
        
        List<ClassDTO> beautyList = ms.searchTopRatedClassList(categoryCode); 
        model.addAttribute("classList", beautyList); 
        return "topCategory/popularBeauty"; 
    }

    // 3. 인기 베이킹 랭킹 페이지
    @GetMapping("/topCategory/baking")
    public String bakingPage(HttpSession session, 
                             @RequestParam(value = "categoryCode", defaultValue = "2") int categoryCode, 
                             Model model) { 
        Member loginMember = (Member) session.getAttribute("loginMember"); 
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
            List<Long> bookmarkedClassCodes = bookmarkService.getBookmarkClassCodes(loginMember.getMemberCode());
            model.addAttribute("bookmarkedClassCodes", bookmarkedClassCodes);
        }
        
        List<ClassDTO> bakingList = ms.searchTopRatedClassList(categoryCode); 
        model.addAttribute("classList", bakingList); 
        return "topCategory/popularBaking"; 
    }

    // 4. 인기 액티비티 랭킹 페이지
    @GetMapping("/topCategory/activity")
    public String activityPage(HttpSession session, 
                               @RequestParam(value = "categoryCode", defaultValue = "3") int categoryCode, 
                               Model model) { 
        Member loginMember = (Member) session.getAttribute("loginMember"); 
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
            List<Long> bookmarkedClassCodes = bookmarkService.getBookmarkClassCodes(loginMember.getMemberCode());
            model.addAttribute("bookmarkedClassCodes", bookmarkedClassCodes);
        }
        
        List<ClassDTO> activityList = ms.searchTopRatedClassList(categoryCode); 
        model.addAttribute("classList", activityList); 
        return "topCategory/popularActivity"; 
    }

    // 5. 조건별 예약 가능 클래스 검색 (검색 결과 페이지 세션 및 북마크 보완)
    @GetMapping("/main/search-available")
    public String searchAvailableClass(HttpSession session,
                                       @RequestParam(value = "categoryCode", defaultValue = "0") int categoryCode,
                                       ScheduleDTO scheduleDTO,
                                       Model model) { 
        Member loginMember = (Member) session.getAttribute("loginMember"); 
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
            List<Long> bookmarkedClassCodes = bookmarkService.getBookmarkClassCodes(loginMember.getMemberCode());
            model.addAttribute("bookmarkedClassCodes", bookmarkedClassCodes);
        }

        List<ClassDTO> availableList = ms.searchAvailableClass(categoryCode, scheduleDTO); 
        List<CategoryDTO> categoryList = ms.getCategoryList(); 
        
        model.addAttribute("availableList", availableList); 
        model.addAttribute("categoryList", categoryList); 
        return "main/availableList"; 
    }

    // 6. 루트 URL 접속 시 /main으로 리다이렉트
    @GetMapping("/")
    public String index() {
        return "redirect:/main"; 
    }
}