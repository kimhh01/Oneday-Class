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
     * @param reservationCode 예약 코드
     */
    @GetMapping("/write")
    public String reviewView(@RequestParam("reservationCode") int reservationCode,
                             HttpSession session,
                             Model model) {
        
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 💡 1. reservationCode 기반으로 리뷰 작성할 예약/클래스 정보 조회
        Review reviewData = reviewService.getReview(reservationCode);
        
        // 💡 2. 본인의 예약 내역이 아닌 경우 접근 차단 (보안 처리)
        if (reviewData == null || reviewData.getMemberCode() != loginMember.getMemberCode()) {
            return "redirect:/mypage/purchase";
        }

        model.addAttribute("purchase", reviewData);

        return "review/review_write";
    }

    /**
     * 리뷰 등록 처리
     * @param rdto 리뷰 데이터 DTO (폼의 hidden input으로 reservationCode, classCode가 자동 바인딩)
     */
    @PostMapping("/write")
    public String writeReview(@ModelAttribute ReviewDTO rdto,
                              HttpSession session,
                              RedirectAttributes rttr) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 💡 3. 작성자 memberCode는 세션값으로 설정하여 전달
        rdto.setMemberCode(loginMember.getMemberCode());
        
        boolean isSuccess = reviewService.writeReview(rdto);

        if (isSuccess) {
            rttr.addFlashAttribute("msg", "리뷰가 성공적으로 등록되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "리뷰 등록 중 오류가 발생했습니다.");
        }

        return "redirect:/mypage/purchase";
    }
}