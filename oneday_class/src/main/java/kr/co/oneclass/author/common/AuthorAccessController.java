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
    
 // 💡 추가: 마이페이지 모달에서 [시작하기] 버튼 클릭 시 동작
    @GetMapping("/author/start")
    public String startAuthor(HttpSession session) {
        // 1. 세션에서 로그인 회원 정보 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 2. 작가 데이터가 없으면 '승인' 상태로 자동 INSERT (이미 존재 시 기존 데이터 반환)
        authorSessionService.getOrCreateAuthor(loginMember.getMemberCode());

        // 3. 바로 작가 대시보드 페이지로 이동 (인터셉터에서 '승인' 상태 확인 후 통과됨)
        return "redirect:/author";
    }
}
