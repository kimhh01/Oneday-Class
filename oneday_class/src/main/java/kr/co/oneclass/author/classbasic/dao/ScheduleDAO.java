package kr.co.oneclass.author.classbasic.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.classbasic.dto.RepeatScheduleDTO;
import kr.co.oneclass.author.classbasic.dto.ScheduleDTO;
import kr.co.oneclass.author.classmanage.dto.ScheduleManageDTO;

@Repository
public class ScheduleDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classbasic.dao.ScheduleDAO.";

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
        return null;
    }

    // 클래스 관리용 단일 일정의 모집 현황을 조회한다
    public ScheduleManageDTO selectScheduleManage(int scheduleCode) {
        return null;
    }

    // 일정의 남은 모집 인원을 변경한다
    public int updateRemainingPeople(int scheduleCode, int remainingPeople) {
        return 0;
    }

    // 일정의 상태를 변경한다
    public int updateScheduleStatus(int scheduleCode, String scheduleStatus) {
        return 0;
    }
}
