package kr.co.oneclass.author.classmanage.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.classmanage.dto.ClassManagementDTO;

@Repository
public class ClassManagementDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classmanage.dao.ClassManagementDAO.";

    private final SqlSessionTemplate sqlSession;

    public ClassManagementDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 운영 클래스 목록을 상태·일정 유형·클래스명 기준으로 검색한다
    public List<ClassManagementDTO> selectClassManagementList(long authorCode, String classStatus,
            String scheduleType, String keyword) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classStatus", classStatus);
        param.put("scheduleType", scheduleType);
        param.put("keyword", keyword);
        return sqlSession.selectList(NAMESPACE + "selectClassManagementList", param);
    }

    // 클래스 관리 상세 상단에 필요한 상태와 요약정보를 조회한다
    public ClassManagementDTO selectClassManagementInfo(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.selectOne(NAMESPACE + "selectClassManagementInfo", param);
    }

    // 해당 클래스의 전체 신청 인원을 조회한다
    public int countClassApplicant(int classCode) {
        return sqlSession.selectOne(NAMESPACE + "countClassApplicant", classCode);
    }

    // 클래스 폐쇄 전 취소되지 않은 향후 예약 수를 조회한다
    public int countActiveReservation(int classCode) {
        return sqlSession.selectOne(NAMESPACE + "countActiveReservation", classCode);
    }

    // 클래스 폐쇄 전 아직 종료되지 않은 일정 수를 조회한다
    public int countActiveSchedule(int classCode) {
        return sqlSession.selectOne(NAMESPACE + "countActiveSchedule", classCode);
    }
}
