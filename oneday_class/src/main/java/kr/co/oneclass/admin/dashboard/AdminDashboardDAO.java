package kr.co.oneclass.admin.dashboard;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminDashboardDAO {

    int selectMemberCount();

    int selectWriterCount();

    int selectClassCount();

    int selectMonthlySales();

    List<AdminMonthlyReservationDTO> selectMonthlyReservationList();
}
