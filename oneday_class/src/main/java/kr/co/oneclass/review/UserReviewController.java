package kr.co.oneclass.review;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mypage/review")
public class UserReviewController {

    @Autowired
    private UserReviewService reviewService;

    /**
     * 리뷰 작성 페이지 이동
     * @param memberCode 회원 코드
     * @param classCode 클래스 코드
     */
    @GetMapping("/write")
    public String reviewView(@RequestParam("memberCode") int memberCode,
                             @RequestParam("classCode") int classCode,
                             HttpSession session,
                             Model model) {
        
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 💡 1. 반환 타입 Review로 변경 및 파라미터 순서(classCode, memberCode) 맞춤
        Review reviewData = reviewService.getReview(classCode, memberCode);
        
        model.addAttribute("purchase", reviewData);
        model.addAttribute("memberCode", memberCode);
        model.addAttribute("classCode", classCode);

        return "review/review_write";
    }

    /**
     * 리뷰 등록 처리
     * @param rdto 리뷰 데이터 DTO (내부에 List<MultipartFile> images 자동 바인딩)
     */
    @PostMapping("/write")
    public String writeReview(@ModelAttribute ReviewDTO rdto,
                              HttpSession session,
                              RedirectAttributes rttr) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        rdto.setMemberCode(loginMember.getMemberCode());
        
        // 💡 2. rdto 단일 파라미터로 서비스 메서드 호출
        boolean isSuccess = reviewService.writeReview(rdto);

        if (isSuccess) {
            rttr.addFlashAttribute("msg", "리뷰가 성공적으로 등록되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "리뷰 등록 중 오류가 발생했습니다.");
        }

        return "redirect:/mypage/purchase";
    }
}