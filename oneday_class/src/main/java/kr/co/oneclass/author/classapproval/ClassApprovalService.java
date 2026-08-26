package kr.co.oneclass.author.classapproval;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.author.classbasic.ClassDAO;

@Service
public class ClassApprovalService {

    private final ClassApprovalDAO caDAO;
    private final ClassDAO cDAO;

    public ClassApprovalService(ClassApprovalDAO caDAO, ClassDAO cDAO) {
        this.caDAO = caDAO;
        this.cDAO = cDAO;
    }

    // 일반 등록 초안은 제외하고 재작성 중인 클래스와 검수·운영 결과를 조회한다
    public List<ClassApprovalDTO> getClassApprovalList(long authorCode, String approvalStatus, String keyword) {
        return caDAO.selectClassApprovalList(authorCode, approvalStatus, keyword);
    }

    // 소유자 조건과 반려 상태를 함께 확인한 뒤 반려 사유를 조회한다
    public String getRejectionReason(long authorCode, int classCode) {
        return caDAO.selectRejectionReason(authorCode, classCode);
    }

    // 반려된 클래스를 재작성 가능한 수정중 상태로 전환한다
    @Transactional
    public boolean reopenRejectedClass(long authorCode, int classCode) {
        return cDAO.reopenRejectedClass(authorCode, classCode) == 1;
    }

    // 승인/대기중 상태를 작가 확인 후 승인/모집중으로 전환한다
    @Transactional
    public boolean startApprovedClass(long authorCode, int classCode) {
        return caDAO.startApprovedClass(authorCode, classCode) == 1;
    }

    public String getSuspensionReason(long authorCode, int classCode) {
        return caDAO.selectSuspensionReason(authorCode, classCode);
    }

    // 중지/대기중 상태를 작가 확인 후 수정중/준비중으로 전환한다
    @Transactional
    public boolean reopenSuspendedClass(long authorCode, int classCode) {
        return caDAO.reopenSuspendedClass(authorCode, classCode) == 1;
    }

    // 기존 호출부 호환용 이름. 재제출은 등록 미리보기의 최종 제출에서 수행한다
    public boolean resubmitClass(long authorCode, int classCode) {
        return reopenRejectedClass(authorCode, classCode);
    }
}
