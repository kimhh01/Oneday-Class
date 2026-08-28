package kr.co.oneclass.admin.dashboard;

import java.util.List;

public interface AdminDashboardService {

    AdminDashboardDomain getDashboard();

    List<AdminMonthlyReservationDomain> getMonthlyReservationList();
}
