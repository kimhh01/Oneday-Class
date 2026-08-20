package kr.co.oneclass.purchase;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
public class PurchaseController {

    @Autowired
    private PurchaseService ps;

    @Autowired
    private ProfileService profileService;

    /**
     * 1. 구매 내역 목록 조회
     */
    @GetMapping("/purchase")
    public String purchaseList(@RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                               Model model,
                               HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        int memberCode = loginMember.getMemberCode();

        // 사이드바 프로필 정보 세팅
        Member member = profileService.getProfile(String.valueOf(memberCode));
        model.addAttribute("member", member != null ? member : loginMember);

        // 구매 내역 목록 조회
        List<Purchase> purchaseList = ps.getPurchaseList(String.valueOf(memberCode), status);

        model.addAttribute("purchaseList", purchaseList);
        model.addAttribute("selectedStatus", status);

        return "purchase/purchase";
    }

    /**
     * 2. 구매 내역 상세 조회
     */
    @GetMapping("/purchase/detail")
    public String purchaseDetail(@RequestParam("reservationCode") String reservationCode,
                                 Model model,
                                 HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        Member member = profileService.getProfile(String.valueOf(loginMember.getMemberCode()));
        model.addAttribute("member", member != null ? member : loginMember);

        // 구매 상세 정보 조회
        Purchase purchaseDetail = ps.getPurchaseDetail(reservationCode);
        model.addAttribute("purchase", purchaseDetail);

        return "purchase/purchase_detail";
    }

    /**
     * 3. 구매 내역 취소 (수강완료/이미 취소 건 검증 및 트랜잭션 결과 반환)
     */
    @PostMapping("/purchase/cancel")
    @ResponseBody
    public Map<String, Object> cancelPurchase(@RequestParam("reservationCode") String reservationCode,
                                              HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        // 💡 서비스에서 세부 검증 결과(성공여부 및 사유 메시지)가 담긴 Map을 직접 반환
        return ps.cancelPurchase(reservationCode, loginMember.getMemberCode());
    }
}