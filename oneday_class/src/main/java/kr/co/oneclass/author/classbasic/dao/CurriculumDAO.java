package kr.co.oneclass.author.classbasic.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.classbasic.dto.CurriculumStepDTO;

@Repository
public class CurriculumDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classbasic.dao.CurriculumDAO.";

    private final SqlSessionTemplate sqlSession;

    public CurriculumDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 클래스의 커리큘럼 단계 목록을 조회한다
    public List<CurriculumStepDTO> selectCurriculumStepList(long authorCode, int classCode) {
        java.util.Map<String, Object> param = new java.util.HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.selectList(NAMESPACE + "selectCurriculumStepList", param);
    }

    // 기존 커리큘럼 단계를 삭제한다
    public int deleteCurriculumStepList(int classCode) {
        return sqlSession.delete(NAMESPACE + "deleteCurriculumStepList", classCode);
    }

    // 커리큘럼 단계를 등록한다
    public int insertCurriculumStep(CurriculumStepDTO csDTO) {
        return sqlSession.insert(NAMESPACE + "insertCurriculumStep", csDTO);
    }
}
