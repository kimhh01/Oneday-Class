package kr.co.oneclass.author.classapproval;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class ClassApprovalDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.classapproval.ClassApprovalDAO.";

    private final SqlSessionTemplate sqlSession;

    public ClassApprovalDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 작가별 클래스 검수 목록을 상태와 클래스명으로 검색한다
    public List<ClassApprovalDTO> selectClassApprovalList(long authorCode, String approvalStatus, String keyword) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("approvalStatus", approvalStatus);
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

    // 관리자 승인 후 작가 확인 대기 중인 클래스를 모집중으로 전환한다
    public int startApprovedClass(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.update(NAMESPACE + "startApprovedClass", param);
    }

    // 운영 중지 사유를 작가 소유권과 중지 상태를 함께 확인해 조회한다
    public String selectSuspensionReason(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.selectOne(NAMESPACE + "selectSuspensionReason", param);
    }

    // 작가가 중지를 확인한 뒤 같은 클래스 코드를 작성중 초안으로 전환한다
    public int reopenSuspendedClass(long authorCode, int classCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("classCode", classCode);
        return sqlSession.update(NAMESPACE + "reopenSuspendedClass", param);
    }
}
