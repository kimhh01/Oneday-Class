package kr.co.oneclass.member.controller;

import kr.co.oneclass.member.domain.Member;
import kr.co.oneclass.member.dto.IdFindDTO;
import kr.co.oneclass.member.dto.LoginDTO;
import kr.co.oneclass.member.dto.OAuthLoginDTO;
import kr.co.oneclass.member.dto.PassFindDTO;
import kr.co.oneclass.member.dto.SignUpDTO;
import kr.co.oneclass.member.service.EmailAuthService;
import kr.co.oneclass.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Spring Boot 3.x 기준 jakarta.servlet 사용. Boot 2.x라면 javax.servlet.http.HttpSession 으로 변경하세요.
import jakarta.servlet.http.HttpSession;

/**
 * Thymeleaf 뷰 리졸버를 사용한다고 가정합니다.
 * 뷰 이름 "member/login" -> src/main/resources/templates/member/login.html 로 매핑됩니다.
 */
@Controller
public class MemberController {

    @Autowired
    private MemberService ms;

    @Autowired
    private EmailAuthService eas;

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
        
        // 1. 아이디가 없거나 비밀번호가 틀린 경우
        if (member == null) {
            ra.addFlashAttribute("errorMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/member/login/general"; // 실패 시 로그인 페이지로 다시 이동
        }
        
        // 2. 로그인 성공 시 세션 저장 후 메인 페이지로 이동
        session.setAttribute("loginMember", member);
        return "redirect:/";
    }

    @PostMapping("/member/oauthLogin")
    public String oauthLogin(OAuthLoginDTO oauthDTO, HttpSession session) {
        Member member = ms.oAuthLogin(oauthDTO);
        if (member == null) {
            // 최초 로그인 -> 자동 회원가입 후 재조회
            ms.oAuthSignUp(oauthDTO);
            member = ms.oAuthLogin(oauthDTO);
        }
        session.setAttribute("loginMember", member);
        return "redirect:/";
    }

    @GetMapping("/member/signUp")
    public String signUpForm(Model model) {
        model.addAttribute("signUpDTO", new SignUpDTO()); // th:object 바인딩용
        return "member/signUp";
    }

    @PostMapping("/member/signUp")
    public String signUp(SignUpDTO signUpDTO, RedirectAttributes ra) {
        boolean result = ms.signUp(signUpDTO);
        
        if (result) {
            // 회원가입 성공 시 완료 화면으로 리다이렉트
            return "redirect:/member/signUpSuccess";
        } else {
            // 실패 시 기존 가입 페이지로 이동
            ra.addFlashAttribute("errorMsg", "회원가입 처리에 실패했습니다. 다시 시도해 주세요.");
            return "redirect:/member/signUp";
        }
    }

    @GetMapping("/member/idDupCheck")
    public String idDupCheckForm(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "member/idDupCheckResult";
    }

    // 다이어그램상 이름이 같은 오버로드는 Ajax(중복확인) 전용으로 분리했습니다.
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
        return "member/signUpSuccess"; // templates/member/signUpSuccess.html
    }

 // 2. 인증번호 발송 AJAX (/member/sendAuthCode)
    @ResponseBody
    @PostMapping("/member/sendAuthCode")
    public String sendAuthCode(@RequestParam("name") String name, @RequestParam("email") String email) {
        // 이름과 이메일로 회원 정보 확인
        IdFindDTO checkDto = new IdFindDTO();
        checkDto.setName(name);
        checkDto.setEmail(email);

        String foundId = ms.findId(checkDto);
        if (foundId == null) {
            return "NOT_FOUND"; // 일치하는 회원이 없음
        }

        // 인증번호 생성 + DB 저장 + 이메일 전송 (Type = "id")
        boolean isSent = eas.sendCode(email, "id");
        
        return isSent ? "OK" : "FAIL";
    }
    
 // 2. 인증번호 발송 AJAX (type = "pass" 로 email_auth 테이블 저장 및 메일 전송)
    @ResponseBody
    @PostMapping("/member/sendPasswordAuthCode")
    public String sendPasswordAuthCode(@RequestParam("id") String id,
                                       @RequestParam("name") String name,
                                       @RequestParam("email") String email) {
        PassFindDTO checkDto = new PassFindDTO();
        checkDto.setId(id);
        checkDto.setName(name);
        checkDto.setEmail(email);

        // 회원 정보 일치 여부 검증
        boolean exists = ms.existsMemberForPassword(checkDto);
        if (!exists) {
            return "NOT_FOUND";
        }

        // EmailAuthService로 type="pass" 저장 및 인증번호 메일 발송
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

 // 3. 아이디 찾기 제출 (인증번호 검증 후 아이디 출력)
    @PostMapping("/member/findId")
    public String findId(IdFindDTO idFindDTO, Model model, RedirectAttributes ra) {
        // 1. 이메일 인증번호 검증 (DB 조회 및 5분 유효시간 체크)
        boolean isVerified = eas.verifyCode(idFindDTO.getEmail(), idFindDTO.getAuthCode());

        if (!isVerified) {
            ra.addFlashAttribute("errorMsg", "인증번호가 일치하지 않거나 유효시간(5분)이 만료되었습니다.");
            return "redirect:/member/findId";
        }

        // 2. 아이디 조회
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

 // 3. 비밀번호 찾기 제출
    @PostMapping("/member/findPassword")
    public String findPassword(PassFindDTO passFindDTO, Model model, RedirectAttributes ra) {
        // 1. EmailAuthService를 이용한 5분 유효성 & 인증코드 검증
        boolean isVerified = eas.verifyCode(passFindDTO.getEmail(), passFindDTO.getAuthCode());

        if (!isVerified) {
            ra.addFlashAttribute("errorMsg", "인증번호가 일치하지 않거나 유효시간(5분)이 만료되었습니다.");
            return "redirect:/member/findPassword";
        }

        // 2. DB 임시 비밀번호 업데이트 & EmailAuthService를 통한 메일 발송
        boolean isUpdated = ms.findPass(passFindDTO);

        if (isUpdated) {
            return "member/findPasswordResult"; // 성공 결과 페이지
        } else {
            ra.addFlashAttribute("errorMsg", "임시 비밀번호 발급 처리 중 오류가 발생했습니다.");
            return "redirect:/member/findPassword";
        }
    }
}
