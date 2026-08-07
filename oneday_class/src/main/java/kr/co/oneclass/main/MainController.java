package kr.co.oneclass.main;

import kr.co.oneclass.member.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // 로그인 성공 시 세션에 담았던 회원 정보 꺼내기
        Member loginMember = (Member) session.getAttribute("loginMember");
        
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
        }
        
        return "index"; // src/main/resources/templates/index.html 로 이동
    }
}