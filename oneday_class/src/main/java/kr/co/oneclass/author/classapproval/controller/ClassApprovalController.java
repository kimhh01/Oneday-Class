package kr.co.oneclass.author.classapproval.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.util.AuthorSessionUtils;
import kr.co.oneclass.author.classapproval.service.ClassApprovalService;

@Controller
public class ClassApprovalController {

    private final ClassApprovalService caService;

    public ClassApprovalController(ClassApprovalService caService) {
        this.caService = caService;
    }

    // 검수 대기·반려·승인 완료 클래스를 검색하고 목록 화면을 출력한다
    @GetMapping("/author/class-approval")
    public String classApprovalList(
            @RequestParam(value = "classStatus", required = false) String classStatus,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            HttpSession session) {

        model.addAttribute("approvals",
                caService.getClassApprovalList(AuthorSessionUtils.getAuthorCode(session), classStatus, keyword));
        model.addAttribute("classStatus", classStatus);
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

    // 검수 단계에서 삭제할 수 있는 클래스를 삭제한다
    @PostMapping("/author/class-approval/{classCode}/delete")
    public String deleteClass(
            @PathVariable("classCode") int classCode,
            HttpSession session) {

        return "redirect:/author/class-approval";
    }
}
