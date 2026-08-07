package kr.co.oneclass.author.classapproval.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.classapproval.dto.ClassApprovalDTO;

@Repository
public class ClassApprovalDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classapproval.dao.ClassApprovalDAO.";

    private final SqlSessionTemplate sqlSession;

    public ClassApprovalDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 작가별 클래스 검수 목록을 상태와 클래스명으로 검색한다
    public List<ClassApprovalDTO> selectClassApprovalList(long authorCode, String classStatus, String keyword) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classStatus", classStatus);
        param.put("keyword", keyword);
        return sqlSession.selectList(NAMESPACE + "selectClassApprovalList", param);
    }

    // 해당 작가 클래스의 반려 사유를 조회한다
    public String selectRejectionReason(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.selectOne(NAMESPACE + "selectRejectionReason", param);
    }
}
