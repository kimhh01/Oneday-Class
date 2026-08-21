package kr.co.oneclass.author.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.member.Member;

@Controller
public class AuthorAccessController {

	@Autowired
    private AuthorSessionService authorSessionService;

	@GetMapping("/author/access")
    public String accessGuide(
            @RequestParam(value = "reason", required = false) String reason,
            Model model) {
        boolean pending = "pending".equals(reason);
        model.addAttribute("accessTitle", pending ? "작가 승인 대기 중입니다" : "작가 등록이 필요합니다");
        model.addAttribute("accessMessage", pending
                ? "관리자 승인이 완료되면 작가 페이지를 이용할 수 있습니다."
                : "현재 계정에 연결된 작가 정보가 없습니다.");
        return "author/access-guide";
    }
    
    // 마이페이지 모달에서 [시작하기] 버튼 클릭 시 관리자 승인 대기 작가를 등록한다
    @GetMapping("/author/start")
    public String startAuthor(HttpSession session) {
        // 1. 세션에서 로그인 회원 정보 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 작가 데이터가 없으면 '대기' 상태로 INSERT (이미 존재 시 기존 데이터 반환)
        authorSessionService.getOrCreateAuthor(loginMember.getMemberCode());

        // 인터셉터가 승인 여부를 확인하고 미승인 작가는 안내 화면으로 보낸다
        return "redirect:/author";
    }
}
