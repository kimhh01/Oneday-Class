package kr.co.oneclass.author.classapproval.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.author.classapproval.dao.ClassApprovalDAO;
import kr.co.oneclass.author.classapproval.dto.ClassApprovalDTO;
import kr.co.oneclass.author.classbasic.dao.ClassDAO;

@Service
public class ClassApprovalService {

    private final ClassApprovalDAO caDAO;
    private final ClassDAO cDAO;

    public ClassApprovalService(ClassApprovalDAO caDAO, ClassDAO cDAO) {
        this.caDAO = caDAO;
        this.cDAO = cDAO;
    }

    // 작성중 초안을 제외한 작가 본인의 검수 목록을 조회한다
    public List<ClassApprovalDTO> getClassApprovalList(long authorCode, String classStatus, String keyword) {
        return caDAO.selectClassApprovalList(authorCode, classStatus, keyword);
    }

    // 소유자 조건과 반려 상태를 함께 확인한 뒤 반려 사유를 조회한다
    public String getRejectionReason(long authorCode, int classCode) {
        return caDAO.selectRejectionReason(authorCode, classCode);
    }

    // 반려된 클래스를 재작성 가능한 작성중 상태로 되돌린다
    @Transactional
    public boolean reopenRejectedClass(long authorCode, int classCode) {
        return cDAO.reopenRejectedClass(authorCode, classCode) == 1;
    }

    // 기존 호출부 호환용 이름. 재제출은 등록 미리보기의 최종 제출에서 수행한다
    public boolean resubmitClass(long authorCode, int classCode) {
        return reopenRejectedClass(authorCode, classCode);
    }

    // 삭제 기능은 연관 데이터 삭제 정책이 확정된 뒤 구현한다
    public boolean removeClass(long authorCode, int classCode) {
        return false;
    }
}
