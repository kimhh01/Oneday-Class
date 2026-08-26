package kr.co.oneclass.author.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.oneclass.common.AESUtil;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DashboardDAO dashboardDAO;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(dashboardDAO);
    }

    @Test
    void dashboardNamesAreDecryptedBeforeRendering() {
        TodayClassDTO todayClass = new TodayClassDTO();
        todayClass.setAuthorName(AESUtil.encrypt("도자기작가"));
        RecentReservationDTO reservation = new RecentReservationDTO();
        reservation.setMemberName(AESUtil.encrypt("홍길동"));
        AuthorSummaryDTO author = new AuthorSummaryDTO();
        author.setAuthorName(AESUtil.encrypt("도자기작가"));

        when(dashboardDAO.selectTodayClassList(7L)).thenReturn(List.of(todayClass));
        when(dashboardDAO.selectRecentReservationList(7L)).thenReturn(List.of(reservation));
        when(dashboardDAO.selectAuthorSummary(7L)).thenReturn(author);

        assertEquals("도자기작가",
                dashboardService.getTodayClassList(7L).get(0).getAuthorName());
        assertEquals("홍길동",
                dashboardService.getRecentReservationList(7L).get(0).getMemberName());
        assertEquals("도자기작가",
                dashboardService.getAuthorSummary(7L).getAuthorName());
    }

    @Test
    void legacyPlaintextNamesRemainUnchanged() {
        RecentReservationDTO reservation = new RecentReservationDTO();
        reservation.setMemberName("기존회원");
        when(dashboardDAO.selectRecentReservationList(7L)).thenReturn(List.of(reservation));

        assertEquals("기존회원",
                dashboardService.getRecentReservationList(7L).get(0).getMemberName());
    }
}
