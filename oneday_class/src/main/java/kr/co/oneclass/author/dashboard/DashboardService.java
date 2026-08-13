package kr.co.oneclass.author.dashboard;

import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class DashboardService {

    private final DashboardDAO dDAO;

    public DashboardService(DashboardDAO dDAO) {
        this.dDAO = dDAO;
    }

    // 모집중 클래스, 미답변 문의, 오늘 예약, 정산 가능금액 요약을 조회한다
    public DashboardSummaryDTO getDashboardSummary(long authorCode) {
        DashboardSummaryDTO summary = dDAO.selectDashboardSummary(authorCode);
        // 템플릿이 요약 지표를 바로 참조하므로 조회 결과가 없어도 빈 객체를 돌려준다
        return summary == null ? new DashboardSummaryDTO() : summary;
    }

    // 오늘 진행하는 클래스 일정 목록을 조회한다
    public List<TodayClassDTO> getTodayClassList(long authorCode) {
        return dDAO.selectTodayClassList(authorCode);
    }

    // 대시보드 알림 목록을 조회한다
    public List<DashboardAlertDTO> getAlertList(long authorCode) {
        return dDAO.selectAlertList(authorCode);
    }

    // 예약 추이 차트 데이터를 조회한다
    public List<ReservationChartDTO> getReservationChart(long authorCode) {
        return dDAO.selectReservationChart(authorCode);
    }

    // 차트 눈금은 데이터 최댓값 이상인 4의 배수로 맞춘다
    public int getReservationChartMax(List<ReservationChartDTO> chart) {
        int maximum = chart.stream()
                .mapToInt(ReservationChartDTO::getReservationCount)
                .max()
                .orElse(0);
        return Math.max(4, ((maximum + 3) / 4) * 4);
    }

    // 최근 예약 내역을 조회한다
    public List<RecentReservationDTO> getRecentReservationList(long authorCode) {
        return dDAO.selectRecentReservationList(authorCode);
    }

    // 최신 공지사항을 조회한다
    public List<DashboardNoticeDTO> getRecentNoticeList() {
        return dDAO.selectRecentNoticeList();
    }

    // 대시보드 상단 작가 요약 정보를 조회한다
    public AuthorSummaryDTO getAuthorSummary(long authorCode) {
        AuthorSummaryDTO author = dDAO.selectAuthorSummary(authorCode);
        // 작가로 등록되지 않은 코드로 들어와도 화면이 깨지지 않도록 빈 객체를 돌려준다
        return author == null ? new AuthorSummaryDTO() : author;
    }
}
