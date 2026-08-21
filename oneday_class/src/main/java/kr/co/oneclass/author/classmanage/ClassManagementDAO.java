package kr.co.oneclass.author.classmanage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class ClassManagementDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classmanage.ClassManagementDAO.";

    private final SqlSessionTemplate sqlSession;

    public ClassManagementDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 운영 클래스 목록을 상태·일정 유형·클래스명 기준으로 검색한다
    public List<ClassManagementDTO> selectClassManagementList(long authorCode, String classStatus,
            String keyword, String fromDate, String toDate, int startRow, int endRow) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classStatus", classStatus);
        param.put("keyword", keyword);
        param.put("fromDate", fromDate);
        param.put("toDate", toDate);
        param.put("startRow", startRow);
        param.put("endRow", endRow);
        return sqlSession.selectList(NAMESPACE + "selectClassManagementList", param);
    }

    // 검색 조건에 맞는 운영 클래스 전체 개수를 조회한다
    public int selectClassManagementCount(long authorCode, String classStatus,
            String keyword, String fromDate, String toDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classStatus", classStatus);
        param.put("keyword", keyword);
        param.put("fromDate", fromDate);
        param.put("toDate", toDate);
        return sqlSession.selectOne(NAMESPACE + "selectClassManagementCount", param);
    }

    // 새 일정 등록 선택창에 표시할 승인·운영 클래스 목록을 조회한다
    public List<ClassManagementDTO> selectSchedulableClassList(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectSchedulableClassList", authorCode);
    }

    // 필터와 무관한 클래스 관리 상단 운영 지표를 조회한다
    public ClassManagementSummaryDTO selectClassManagementSummary(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectClassManagementSummary", authorCode);
    }

    // 작가의 승인 클래스 일정을 날짜·상태·클래스명 기준으로 모아 조회한다
    public List<ScheduleManageDTO> selectAuthorScheduleList(long authorCode, String scheduleStatus,
            String keyword, String fromDate, String toDate) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleStatus", scheduleStatus);
        param.put("keyword", keyword);
        param.put("fromDate", fromDate);
        param.put("toDate", toDate);
        return sqlSession.selectList(NAMESPACE + "selectAuthorScheduleList", param);
    }

    // 클래스 관리 상세 상단에 필요한 상태와 요약정보를 조회한다
    public ClassManagementDTO selectClassManagementInfo(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.selectOne(NAMESPACE + "selectClassManagementInfo", param);
    }

    // 승인된 클래스의 운영 상태를 현재 상태 조건과 함께 변경한다
    public int updateClassOperationalStatus(long authorCode, int classCode,
            String currentStatus, String nextStatus) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        param.put("currentStatus", currentStatus);
        param.put("nextStatus", nextStatus);
        return sqlSession.update(NAMESPACE + "updateClassOperationalStatus", param);
    }

    // 모든 모집 일정과 예약이 정리된 승인 클래스를 폐강 상태로 변경한다
    public int updateClassClosed(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.update(NAMESPACE + "updateClassClosed", param);
    }

    // 폐강 본인 확인에 사용할 작가 회원의 일반 로그인 비밀번호를 조회한다
    public String selectAuthorPassword(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectAuthorPassword", authorCode);
    }

    // 선택한 클래스의 결제 완료·미환불 누적 매출을 조회한다
    public long selectClassSalesAmount(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        Long amount = sqlSession.selectOne(NAMESPACE + "selectClassSalesAmount", param);
        return amount == null ? 0L : amount;
    }

    // 해당 클래스의 전체 신청 인원을 조회한다
    public int countClassApplicant(int classCode) {
        return sqlSession.selectOne(NAMESPACE + "countClassApplicant", classCode);
    }

    // 클래스 폐강 전 취소되지 않은 향후 예약 수를 조회한다
    public int countActiveReservation(int classCode) {
        return sqlSession.selectOne(NAMESPACE + "countActiveReservation", classCode);
    }

    // 클래스 폐강 전 아직 모집 가능한 일정 수를 조회한다
    public int countRecruitingSchedule(int classCode) {
        return sqlSession.selectOne(NAMESPACE + "countRecruitingSchedule", classCode);
    }

    // 일정 예약 결제 중 이미 정산에 포함된 건수를 조회한다
    public int countCalculatedSchedulePayment(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.selectOne(NAMESPACE + "countCalculatedSchedulePayment", param);
    }

    // 일정 취소로 전액 환불해야 하는 결제 완료 건수를 조회한다
    public int countRefundableSchedulePayment(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.selectOne(NAMESPACE + "countRefundableSchedulePayment", param);
    }

    // 일정 취소 전에 Toss에서 전액 취소할 결제 키 목록을 조회한다
    public List<ScheduleRefundPaymentDTO> selectRefundableSchedulePaymentList(
            long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.selectList(NAMESPACE + "selectRefundableSchedulePaymentList", param);
    }

    // 일정의 결제 완료 건을 전액 환불 상태로 변경한다
    public int refundSchedulePayments(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.update(NAMESPACE + "refundSchedulePayments", param);
    }

    // 일정의 예약 상태를 취소로 변경한다
    public int cancelScheduleReservations(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.update(NAMESPACE + "cancelScheduleReservations", param);
    }
}
