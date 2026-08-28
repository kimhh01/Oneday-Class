package kr.co.oneclass.author.dashboard;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.AuthorSessionUtils;

@Controller
public class DashboardController {

    private final DashboardService dService;

    public DashboardController(DashboardService dService) {
        this.dService = dService;
    }

    // 작가센터 대시보드 화면
    @GetMapping("/author")
    public String dashboard(Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);

        model.addAttribute("dashboardSummary", dService.getDashboardSummary(authorCode));
        model.addAttribute("todayClasses", dService.getTodayClassList(authorCode));
        model.addAttribute("dashboardAlerts", dService.getAlertList(authorCode));
        List<ReservationChartDTO> reservationChart = dService.getReservationChart(authorCode);
        model.addAttribute("reservationChart", reservationChart);
        model.addAttribute("reservationChartMax", dService.getReservationChartMax(reservationChart));
        model.addAttribute("recentReservations", dService.getRecentReservationList(authorCode));
        model.addAttribute("recentNotices", dService.getRecentNoticeList());
        model.addAttribute("authorSummary", dService.getAuthorSummary(authorCode));
        return "author/index";
    }
}
