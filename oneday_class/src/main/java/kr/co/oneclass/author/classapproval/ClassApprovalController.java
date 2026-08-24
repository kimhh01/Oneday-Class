package kr.co.oneclass.author.classapproval;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.AuthorSessionUtils;

@Controller
public class ClassApprovalController {

    private final ClassApprovalService caService;

    public ClassApprovalController(ClassApprovalService caService) {
        this.caService = caService;
    }

    // 검수 대기·반려·승인 완료 클래스를 검색하고 목록 화면을 출력한다
    @GetMapping("/author/class-approval")
    public String classApprovalList(
            @RequestParam(value = "approvalStatus", required = false) String approvalStatus,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            HttpSession session) {

        model.addAttribute("approvals",
                caService.getClassApprovalList(AuthorSessionUtils.getAuthorCode(session), approvalStatus, keyword));
        model.addAttribute("approvalStatus", approvalStatus);
        model.addAttribute("keyword", keyword);
        return "author/class-approval";
    }

    // 선택한 클래스의 반려 사유를 조회한다
    @GetMapping("/author/class-approval/{classCode}/rejection")
    public String rejectionReason(
            @PathVariable("classCode") int classCode,
            Model model,
            HttpSession session) {

        String reason = caService.getRejectionReason(AuthorSessionUtils.getAuthorCode(session), classCode);
        if (reason == null) {
            return "redirect:/author/class-approval";
        }
        model.addAttribute("classCode", classCode);
        model.addAttribute("rejectionReason", reason);
        return "author/class-rejection-detail";
    }

    // 예전 수정 링크는 상태를 바꾸지 않고 반려 사유 화면으로 안내한다
    @GetMapping("/author/class-approval/{classCode}/edit")
    public String editRejectedClass(
            @PathVariable("classCode") int classCode,
            HttpSession session) {

        String reason = caService.getRejectionReason(AuthorSessionUtils.getAuthorCode(session), classCode);
        if (reason == null) {
            return "redirect:/author/class-approval";
        }
        return "redirect:/author/class-approval/" + classCode + "/rejection";
    }

    // 재작성 버튼을 눌렀을 때만 반려 클래스를 작성중으로 되돌린다
    @PostMapping("/author/class-approval/{classCode}/resubmit")
    public String resubmitClass(
            @PathVariable("classCode") int classCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        String reason = caService.getRejectionReason(authorCode, classCode);
        if (reason == null || !caService.reopenRejectedClass(authorCode, classCode)) {
            redirectAttributes.addFlashAttribute("approvalError", "재작성할 수 있는 반려 클래스가 아닙니다.");
            return "redirect:/author/class-approval";
        }
        redirectAttributes.addFlashAttribute("rejectionNotice", "반려 사유: " + reason);
        return "redirect:/author/classes/register/basic?classCode=" + classCode;
    }

    // 관리자가 승인한 뒤 대기중인 클래스를 작가가 확인하고 모집 시작한다
    @PostMapping("/author/class-approval/{classCode}/start")
    public String startApprovedClass(
            @PathVariable("classCode") int classCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!caService.startApprovedClass(AuthorSessionUtils.getAuthorCode(session), classCode)) {
            redirectAttributes.addFlashAttribute("approvalError", "모집을 시작할 수 있는 승인 클래스가 아닙니다.");
        } else {
            redirectAttributes.addFlashAttribute("approvalMessage", "클래스 모집을 시작했습니다.");
        }
        return "redirect:/author/class-approval";
    }

    // 운영 중지를 확인한 뒤 작성중/준비중 상태로 바꾸고 수정 화면으로 이동한다
    @PostMapping("/author/class-approval/{classCode}/suspension/edit")
    public String editSuspendedClass(
            @PathVariable("classCode") int classCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        String reason = caService.getSuspensionReason(authorCode, classCode);
        if (reason == null || !caService.reopenSuspendedClass(authorCode, classCode)) {
            redirectAttributes.addFlashAttribute("approvalError", "수정할 수 있는 중지 클래스가 아닙니다.");
            return "redirect:/author/class-approval";
        }
        redirectAttributes.addFlashAttribute("rejectionNotice", "운영 중지 사유: " + reason);
        return "redirect:/author/classes/register/basic?classCode=" + classCode;
    }
}
