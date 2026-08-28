package kr.co.oneclass.admin.dashboard;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private AdminDashboardDAO adminDashboardDAO;

	public AdminDashboardServiceImpl(AdminDashboardDAO adminDashboardDAO) {
		this.adminDashboardDAO = adminDashboardDAO;
	}

	@Override
	public AdminDashboardDomain getDashboard() {

		AdminDashboardDTO dashboardDTO = new AdminDashboardDTO();

		dashboardDTO.setMemberCount(adminDashboardDAO.selectMemberCount());

		dashboardDTO.setWriterCount(adminDashboardDAO.selectWriterCount());

		dashboardDTO.setClassCount(adminDashboardDAO.selectClassCount());

		dashboardDTO.setMonthlySales(adminDashboardDAO.selectMonthlySales());

		return dashboardDTO.toDomain();
	}

	@Override
	public List<AdminMonthlyReservationDomain> getMonthlyReservationList() {

		return adminDashboardDAO.selectMonthlyReservationList().stream().map(AdminMonthlyReservationDTO::toDomain)
				.toList();
	}
}
