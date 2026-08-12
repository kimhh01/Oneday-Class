package kr.co.oneclass.member;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.member.IdFindDTO;
import kr.co.oneclass.member.LoginDTO;
import kr.co.oneclass.member.OAuthLoginDTO;
import kr.co.oneclass.member.PassFindDTO;
import kr.co.oneclass.member.SignUpDTO;
import kr.co.oneclass.member.EmailAuthService;
import kr.co.oneclass.member.MemberService;
import kr.co.oneclass.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

    @Autowired
    private MemberService ms;

    @Autowired
    private EmailAuthService eas;

    @Autowired
    private ProfileService ps;

    @GetMapping("/member/login")
    public String choiceLoginForm() {
        return "member/login";
    }

    @GetMapping("/member/login/general")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "member/generalLogin";
    }

    @PostMapping("/member/login/general")
    public String login(LoginDTO ldto, HttpSession session, RedirectAttributes ra) {
        Member member = ms.login(ldto);
        
        if (member == null) {
            ra.addFlashAttribute("errorMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/member/login/general";
        }
        
        session.setAttribute("loginMember", member);
        return "redirect:/";
    }

    @PostMapping("/member/oauthLogin")
    public String oauthLogin(OAuthLoginDTO oauthDTO, HttpSession session) {
        Member member = ms.oAuthLogin(oauthDTO);
        if (member == null) {
            ms.oAuthSignUp(oauthDTO);
            member = ms.oAuthLogin(oauthDTO);
        }
        session.setAttribute("loginMember", member);
        return "redirect:/";
    }

    @GetMapping("/member/signUp")
    public String signUpForm(Model model) {
        model.addAttribute("signUpDTO", new SignUpDTO());
        return "member/signUp";
    }

    @PostMapping("/member/signUp")
    public String signUp(SignUpDTO signUpDTO, RedirectAttributes ra) {
        boolean result = ms.signUp(signUpDTO);
        
        if (result) {
            return "redirect:/member/signUpSuccess";
        } else {
            ra.addFlashAttribute("errorMsg", "회원가입 처리에 실패했습니다. 다시 시도해 주세요.");
            return "redirect:/member/signUp";
        }
    }

    @GetMapping("/member/idDupCheck")
    public String idDupCheckForm(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "member/idDupCheckResult";
    }

    @ResponseBody
    @PostMapping("/member/idDupCheck")
    public String idDupCheckAjax(@RequestParam("id") String id) {
        if (id == null || id.trim().isEmpty()) {
            return "EMPTY";
        }
        
        boolean isDuplicate = ms.isIdDuplicate(id);
        return isDuplicate ? "DUPLICATE" : "OK";
    }
    
    @GetMapping("/member/signUpSuccess")
    public String signUpSuccess() {
        return "member/signUpSuccess";
    }

    @ResponseBody
    @PostMapping("/member/sendAuthCode")
    public String sendAuthCode(@RequestParam("name") String name, @RequestParam("email") String email) {
        IdFindDTO checkDto = new IdFindDTO();
        checkDto.setName(name);
        checkDto.setEmail(email);

        String foundId = ms.findId(checkDto);
        if (foundId == null) {
            return "NOT_FOUND";
        }

        boolean isSent = eas.sendCode(email, "id");
        return isSent ? "OK" : "FAIL";
    }
    
    @ResponseBody
    @PostMapping("/member/sendPasswordAuthCode")
    public String sendPasswordAuthCode(@RequestParam("id") String id,
                                       @RequestParam("name") String name,
                                       @RequestParam("email") String email) {
        PassFindDTO checkDto = new PassFindDTO();
        checkDto.setId(id);
        checkDto.setName(name);
        checkDto.setEmail(email);

        boolean exists = ms.existsMemberForPassword(checkDto);
        if (!exists) {
            return "NOT_FOUND";
        }

        boolean isSent = eas.sendCode(email, "pass");
        return isSent ? "OK" : "FAIL";
    }

    @ResponseBody
    @PostMapping("/member/emailAuthVerify")
    public String emailAuthVerify(@RequestParam String email, @RequestParam String authCode) {
        boolean verified = eas.verifyCode(email, authCode);
        return verified ? "SUCCESS" : "FAIL";
    }

    @GetMapping("/member/findId")
    public String findIdForm(Model model) {
        model.addAttribute("findIdDTO", new IdFindDTO());
        return "member/findId";
    }

    @PostMapping("/member/findId")
    public String findId(IdFindDTO idFindDTO, Model model, RedirectAttributes ra) {
        boolean isVerified = eas.verifyCode(idFindDTO.getEmail(), idFindDTO.getAuthCode());

        if (!isVerified) {
            ra.addFlashAttribute("errorMsg", "인증번호가 일치하지 않거나 유효시간(5분)이 만료되었습니다.");
            return "redirect:/member/findId";
        }

        String foundId = ms.findId(idFindDTO);

        if (foundId != null) {
            model.addAttribute("foundId", foundId);
            return "member/findIdResult";
        } else {
            ra.addFlashAttribute("errorMsg", "일치하는 회원 정보가 없습니다.");
            return "redirect:/member/findId";
        }
    }

    @GetMapping("/member/findPassword")
    public String findPasswordForm(Model model) {
        model.addAttribute("passFindDTO", new PassFindDTO());
        return "member/findPassword";
    }

    @PostMapping("/member/findPassword")
    public String findPassword(PassFindDTO passFindDTO, Model model, RedirectAttributes ra) {
        boolean isVerified = eas.verifyCode(passFindDTO.getEmail(), passFindDTO.getAuthCode());

        if (!isVerified) {
            ra.addFlashAttribute("errorMsg", "인증번호가 일치하지 않거나 유효시간(5분)이 만료되었습니다.");
            return "redirect:/member/findPassword";
        }

        boolean isUpdated = ms.findPass(passFindDTO);

        if (isUpdated) {
            return "member/findPasswordResult";
        } else {
            ra.addFlashAttribute("errorMsg", "임시 비밀번호 발급 처리 중 오류가 발생했습니다.");
            return "redirect:/member/findPassword";
        }
    }

 // ==========================================
    // 회원탈퇴 관련 처리
    // ==========================================

    /**
     * 1. 회원탈퇴 안내 페이지 이동
     */
    @GetMapping("/mypage/withdraw")
    public String withdrawForm(Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        Member member = ps.getProfile(String.valueOf(loginMember.getMemberCode()));
        model.addAttribute("member", member != null ? member : loginMember);

        return "withdraw/withdraw"; // src/main/resources/templates/withdraw/withdraw.html 뷰 반환
    }

    /**
     * 2. 회원탈퇴 실행 처리 (소셜 로그인 분기 포함)
     */
    @PostMapping("/mypage/withdraw")
    public String withdraw(@RequestParam(value = "password", required = false) String password,
                           HttpSession session, 
                           RedirectAttributes ra) {
                           
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 💡 핵심: 일반 회원('LOCAL')일 때만 비밀번호 검증 수행
        if ("LOCAL".equals(loginMember.getLoginType())) {
            if (password == null || password.trim().isEmpty()) {
                ra.addFlashAttribute("errorMsg", "비밀번호를 입력해 주세요.");
                ra.addFlashAttribute("openModal", true);
                return "redirect:/mypage/withdraw";
            }

            boolean isPasswordValid = ms.checkPassword(loginMember.getMemberCode(), password);
            if (!isPasswordValid) {
                ra.addFlashAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
                ra.addFlashAttribute("openModal", true);
                return "redirect:/mypage/withdraw";
            }
        }
        // 소셜 로그인 회원은 password 검증 과정을 스킵하고 바로 아래 DB 탈퇴로 진행됩니다.

        // DB 탈퇴 처리 (member + member_auth 삭제/상태변경)
        boolean result = ms.withdrawMember(String.valueOf(loginMember.getMemberCode()));

        if (result) {
            session.invalidate(); // 세션 무효화
            return "redirect:/member/withdrawSuccess";
        } else {
            ra.addFlashAttribute("errorMsg", "회원 탈퇴 처리 중 오류가 발생했습니다.");
            return "redirect:/mypage/withdraw";
        }
    }

    /**
     * 3. 회원탈퇴 완료 페이지
     */
    @GetMapping("/member/withdrawSuccess")
    public String withdrawSuccess() {
        return "withdraw/withdraw_success"; // src/main/resources/templates/withdraw/withdraw_success.html 뷰 반환
    }
}