package kr.co.oneclass.author.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.dashboard.service.DashboardService;

@Controller
public class DashboardController {

    private final DashboardService dService;

    public DashboardController(DashboardService dService) {
        this.dService = dService;
    }

    // 작가센터 대시보드 화면
    @GetMapping("/author")
    public String dashboard(Model model, HttpSession session) {
        // TODO: 로그인 세션 연결 후 제거 - 세션에서 작가 코드를 꺼내도록 교체
        // CREATOR.OPERATOR_CODE 실제값. 1 은 존재하지 않는 작가라 조회 결과가 비어버린다
        long authorCode = 1010101010L;

        model.addAttribute("dashboardSummary", dService.getDashboardSummary(authorCode));
        model.addAttribute("todayClasses", dService.getTodayClassList(authorCode));
        model.addAttribute("dashboardAlerts", dService.getAlertList(authorCode));
        model.addAttribute("reservationChart", dService.getReservationChart(authorCode));
        model.addAttribute("authorSummary", dService.getAuthorSummary(authorCode));
        return "author/index";
    }
}
