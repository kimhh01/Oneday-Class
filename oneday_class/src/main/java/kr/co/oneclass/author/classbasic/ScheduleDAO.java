package kr.co.oneclass.author.classbasic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.classmanage.ScheduleManageDTO;
import kr.co.oneclass.author.classmanage.ScheduleOperationDTO;

@Repository
public class ScheduleDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classbasic.ScheduleDAO.";

    private final SqlSessionTemplate sqlSession;

    public ScheduleDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 클래스의 반복 일정 규칙 목록을 조회한다
    public List<RepeatScheduleDTO> selectRepeatScheduleList(int classCode) {
        return sqlSession.selectList(NAMESPACE + "selectRepeatScheduleList", classCode);
    }

    // 클래스의 개별 일정 목록을 조회한다
    public List<ScheduleDTO> selectScheduleList(int classCode) {
        return sqlSession.selectList(NAMESPACE + "selectScheduleList", classCode);
    }

    // 기존 반복 일정 규칙을 삭제한다
    public int deleteRepeatScheduleList(int classCode) {
        return sqlSession.delete(NAMESPACE + "deleteRepeatScheduleList", classCode);
    }

    // 기존 개별 일정을 삭제한다
    public int deleteScheduleList(int classCode) {
        return sqlSession.delete(NAMESPACE + "deleteScheduleList", classCode);
    }

    // 반복 일정 규칙을 등록한다
    public int insertRepeatSchedule(RepeatScheduleDTO rsDTO) {
        return sqlSession.insert(NAMESPACE + "insertRepeatSchedule", rsDTO);
    }

    // 개별 일정을 등록한다
    public int insertSchedule(ScheduleDTO sDTO) {
        return sqlSession.insert(NAMESPACE + "insertSchedule", sDTO);
    }

    // 클래스 관리용 일정별 모집 현황 목록을 조회한다
    public List<ScheduleManageDTO> selectScheduleManageList(int classCode) {
        return sqlSession.selectList(NAMESPACE + "selectScheduleManageList", classCode);
    }

    // 클래스 관리용 단일 일정의 모집 현황을 조회한다
    public ScheduleManageDTO selectScheduleManage(int scheduleCode) {
        return sqlSession.selectOne(NAMESPACE + "selectScheduleManage", scheduleCode);
    }

    // 일정의 남은 모집 인원을 변경한다
    public int updateRemainingPeople(long authorCode, int scheduleCode, int remainingPeople) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        param.put("remainingPeople", remainingPeople);
        return sqlSession.update(NAMESPACE + "updateRemainingPeople", param);
    }

    // 향후 일정을 더 이상 모집하지 않도록 남은 자리를 0으로 마감한다
    public int closeSchedule(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.update(NAMESPACE + "closeSchedule", param);
    }

    // 예약이 없는 향후 일정의 날짜·시간·정원을 변경한다
    public int updateManagedSchedule(long authorCode, ScheduleOperationDTO schedule) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", schedule.getScheduleCode());
        param.put("scheduleDate", schedule.getScheduleDate());
        param.put("startTime", schedule.getStartTime());
        param.put("endTime", schedule.getEndTime());
        param.put("minPeople", schedule.getMinPeople());
        param.put("maxPeople", schedule.getMaxPeople());
        return sqlSession.update(NAMESPACE + "updateManagedSchedule", param);
    }

    // 일정 변경 후 연결된 반복 규칙의 운영 기간을 실제 일정 범위로 맞춘다
    public int refreshRepeatRuleRange(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.update(NAMESPACE + "refreshRepeatRuleRange", param);
    }

    // 같은 클래스에 동일 날짜·시작 시간 일정이 있는지 확인한다
    public int countDuplicateSchedule(long authorCode, ScheduleOperationDTO schedule) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", schedule.getClassCode());
        param.put("scheduleCode", schedule.getScheduleCode());
        param.put("scheduleDate", schedule.getScheduleDate());
        param.put("startTime", schedule.getStartTime());
        return sqlSession.selectOne(NAMESPACE + "countDuplicateSchedule", param);
    }

    // 모집 마감된 향후 일정에 다시 예약 가능한 자리를 연다
    public int reopenSchedule(long authorCode, int scheduleCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("scheduleCode", scheduleCode);
        return sqlSession.update(NAMESPACE + "reopenSchedule", param);
    }
}
