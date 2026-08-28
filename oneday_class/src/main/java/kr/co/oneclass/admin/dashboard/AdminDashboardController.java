package kr.co.oneclass.admin.dashboard;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

	private AdminDashboardService adminDashboardService;

	public AdminDashboardController(AdminDashboardService adminDashboardService) {

		this.adminDashboardService = adminDashboardService;
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {

		AdminDashboardDomain dashboard = adminDashboardService.getDashboard();

		List<AdminMonthlyReservationDomain> monthlyReservations = adminDashboardService.getMonthlyReservationList();

		model.addAttribute("dashboard", dashboard);
		model.addAttribute("monthlyReservations", monthlyReservations);

		return "admin/dashboard/dashboard";
	}
}
