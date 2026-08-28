package kr.co.oneclass.author.dashboard;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class DashboardDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.dashboard.DashboardDAO.";

    private final SqlSessionTemplate sqlSession;

    public DashboardDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 대시보드 요약 지표를 집계한다
    public DashboardSummaryDTO selectDashboardSummary(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectDashboardSummary", authorCode);
    }

    // 오늘 진행하는 클래스 일정 목록을 조회한다
    public List<TodayClassDTO> selectTodayClassList(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectTodayClassList", authorCode);
    }

    // 대시보드 알림 목록을 조회한다
    public List<DashboardAlertDTO> selectAlertList(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectAlertList", authorCode);
    }

    // 예약 추이 차트 데이터를 집계한다
    public List<ReservationChartDTO> selectReservationChart(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectReservationChart", authorCode);
    }

    // 최근 예약 목록을 조회한다
    public List<RecentReservationDTO> selectRecentReservationList(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectRecentReservationList", authorCode);
    }

    // 대시보드에 노출할 최신 공지사항을 조회한다
    public List<DashboardNoticeDTO> selectRecentNoticeList() {
        return sqlSession.selectList(NAMESPACE + "selectRecentNoticeList");
    }

    // 작가 요약 정보를 조회한다
    public AuthorSummaryDTO selectAuthorSummary(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectAuthorSummary", authorCode);
    }
}
