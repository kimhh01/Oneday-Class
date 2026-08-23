package kr.co.oneclass.main;


import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.member.Member;

import java.util.List;

@Controller
public class MainController {
	
    @Autowired
    private MainService ms;

    // 1. 메인 페이지 (인기 클래스 + 당일/주말 및 카테고리별 데이터 + 로그인 세션 처리)
    @GetMapping("/main")
    public String mainPage(HttpSession session, Model model) {

        // 로그인 회원 정보 세션 확인
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
        }

        // 당일 / 주말 예약 가능 클래스 데이터
        model.addAttribute("todayClasses",  ms.searchTodayClass());
        model.addAttribute("weekendClasses",  ms.searchWeekendClass());

        // 1-1. 이달의 인기 클래스 상위 3개 (전체 카테고리 = 0)
        model.addAttribute("topRatedList",  ms.searchTopRatedClass(0, 3)); 

        // 1-2. 인기 디저트/베이킹 클래스 상위 5개 (베이킹 카테고리 코드 = 2)
        model.addAttribute("bakingList",  ms.searchTopRatedClass(2, 5)); 

        // 1-3. 인기 뷰티 클래스 상위 3개 (뷰티 카테고리 코드 = 1)
        model.addAttribute("beautyList",  ms.searchTopRatedClass(1, 3)); 

        return "main/main"; 
    }

 // 2. 인기 뷰티 랭킹 페이지
    @GetMapping("/top_category/beauty")
    public String beautyPage(HttpSession session, 
                             @RequestParam(value = "categoryCode", defaultValue = "1") int categoryCode, 
                             Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) model.addAttribute("loginMember", loginMember);
        
        List<ClassDTO> beautyList = ms.searchTopRatedClassList(categoryCode);
        model.addAttribute("classList", beautyList);
        return "top_category/popular_beauty";
    }

    // 3. 인기 베이킹 랭킹 페이지
    @GetMapping("/top_category/baking")
    public String bakingPage(HttpSession session, 
                             @RequestParam(value = "categoryCode", defaultValue = "2") int categoryCode, 
                             Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) model.addAttribute("loginMember", loginMember);
        
        List<ClassDTO> bakingList = ms.searchTopRatedClassList(categoryCode);
        model.addAttribute("classList", bakingList);
        return "top_category/popular_baking";
    }

    // 4. 인기 액티비티 랭킹 페이지
    @GetMapping("/top_category/activity")
    public String activityPage(HttpSession session, 
                               @RequestParam(value = "categoryCode", defaultValue = "3") int categoryCode, 
                               Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) model.addAttribute("loginMember", loginMember);
        
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

    // 6. 루트 URL 접속 시 /main으로 리다이렉트
    @GetMapping("/")
    public String index() {
        return "redirect:/main";
    }
}