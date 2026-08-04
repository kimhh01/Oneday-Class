package kr.co.oneclass.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 회원(로그인/회원가입) 관련 화면을 렌더링하는 컨트롤러
 * 실제 패키지 경로는 프로젝트의 base package에 맞게 수정해서 사용하세요.
 *
 * 현재는 화면 진입(View 렌더링)만 담당하며, 로그인/회원가입 처리 등
 * 실제 기능 구현은 아직 진행하지 않았습니다.
 */
@Controller
public class MemberController {

    /**
     * 로그인 선택 화면 (일반 로그인 / 구글 로그인)
     */
    @GetMapping("/member/login")
    public String loginForm() {
        return "member/login";
    }

    /**
     * 일반 로그인(아이디/비밀번호) 입력 화면
     */
    @GetMapping("/member/login/general")
    public String generalLoginForm(Model model) {
    	model.addAttribute("loginDTO", new LoginDTO());
        return "member/generalLogin";
    }

    /**
     * 일반계정 회원가입 화면
     */
    @GetMapping("/member/signup")
    public String signupForm(Model model) {

        model.addAttribute("signUpDTO", new SignUpDTO());

        return "member/signup";
    }

    /**
     * 아이디 찾기 화면
     */
    @GetMapping("/member/findId")
    public String findIdForm(Model model) {
    	model.addAttribute("findIdDTO", new FindIdDTO());
        return "member/findId";
    }

    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping("/member/findPassword")
    public String findPasswordForm(Model model) {
    	model.addAttribute("findPasswordDTO", new FindPasswordDTO());
        return "member/findPassword";
    }
}
