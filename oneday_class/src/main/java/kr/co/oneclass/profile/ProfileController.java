package kr.co.oneclass.profile;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.PassChangeDTO;
import kr.co.oneclass.profile.ProfileDTO;
import kr.co.oneclass.profile.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService ps;

    @GetMapping
    public String profileForm(Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        Member member = ps.getProfile(String.valueOf(loginMember.getMemberCode()));
        model.addAttribute("member", member != null ? member : loginMember);

        return "profile/profile";
    }
 // 💡 properties의 /app/upload/ (마운트 경로) 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/changeProfileImg")
    public String changeProfileImgForm(HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/member/login";
        }
        return "profile/changeProfileImg";
    }

    @PostMapping("/changeProfileImg")
    public String changeProfileImg(@RequestParam(value = "image", required = false) MultipartFile image,
                                   @RequestParam(value = "imageMode", defaultValue = "FILE") String imageMode,
                                   HttpSession session, RedirectAttributes rttr) {
        
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        if ("DEFAULT".equals(imageMode)) {
            // 웹 브라우저에서 접근할 기본 프로필 이미지 URL
            String defaultPath = "/upload/profile/default/default_profile.png";

            ps.changeProfileImg(String.valueOf(loginMember.getMemberCode()), defaultPath);
            loginMember.setProfileImg(defaultPath);
            session.setAttribute("loginMember", loginMember);
            
            rttr.addFlashAttribute("msg", "기본 프로필 이미지로 변경되었습니다.");
        } else if (image != null && !image.isEmpty()) {
            try {
                // 1. 실제 파일이 저장될 물리 디렉터리 경로 (/app/upload/profile/)
                String saveDirPath = uploadDir + "profile/";
                File dir = new File(saveDirPath);
                if (!dir.exists()) {
                    dir.mkdirs(); // 디렉터리가 없으면 생성
                }

                // 2. 파일명 중복 방지 처리
                String savedFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File dest = new File(saveDirPath + savedFilename);

                // 3. 파일 저장 실행
                image.transferTo(dest);

                // 4. DB 및 웹 브라우저에서 사용할 URL 경로 (/upload/profile/파일명)
                String imgPath = "/upload/profile/" + savedFilename;
                boolean isImgChanged = ps.changeProfileImg(String.valueOf(loginMember.getMemberCode()), imgPath);

                if (isImgChanged) {
                    loginMember.setProfileImg(imgPath);
                    session.setAttribute("loginMember", loginMember);
                    rttr.addFlashAttribute("msg", "프로필 이미지가 변경되었습니다.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                rttr.addFlashAttribute("msg", "이미지 업로드 중 오류가 발생했습니다.");
            }
        }

        return "redirect:/profile";
    }

    @GetMapping("/changePassword")
    public String changePasswordForm(HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/member/login";
        }
        return "profile/changePassword";
    }

    @PostMapping("/changePassword")
    @ResponseBody // 💡 페이지 이동 대신 JSON 데이터를 반환합니다.
    public Map<String, Object> changePassword(PassChangeDTO pdto, 
                                             @RequestParam(value = "newPassConfirm", required = false) String newPassConfirm,
                                             HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        String currentPass = pdto.getCurrentPass();
        String newPass = pdto.getNewPass();

        // 1. 기존 비밀번호와 새 비밀번호 동일 여부 체크
        if (currentPass != null && currentPass.equals(newPass)) {
            result.put("success", false);
            result.put("message", "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");
            return result;
        }

        // 2. 새 비밀번호 확인 일치 여부 체크
        if (newPassConfirm != null && !newPass.equals(newPassConfirm)) {
            result.put("success", false);
            result.put("message", "새 비밀번호와 확인용 비밀번호가 일치하지 않습니다.");
            return result;
        }

        // 3. 새 비밀번호 조건(영문+숫자+특수문자 조합 8~16자) 정규식 체크
        String passRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,16}$";
        if (newPass == null || !newPass.matches(passRegex)) {
            result.put("success", false);
            result.put("message", "새 비밀번호는 8~16자의 영문, 숫자, 특수문자 조합이어야 합니다.");
            return result;
        }

        // 4. DB 검증 및 업데이트 (서비스에서 현재 비밀번호 비교)
        pdto.setMemberCode(loginMember.getMemberCode());
        boolean isChanged = ps.changePassword(pdto);

        if (isChanged) {
            result.put("success", true);
            result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            // 💡 현재 비밀번호가 틀린 경우
            result.put("success", false);
            result.put("message", "현재 비밀번호가 올바르지 않습니다.");
        }

        return result;
    }

    @PostMapping("/updateProfile")
    public String updateProfile(ProfileDTO pdto, HttpSession session, RedirectAttributes rttr) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        pdto.setMemberCode(loginMember.getMemberCode());
        if (ps.updateProfile(pdto)) {
            loginMember.setName(pdto.getName());
            loginMember.setPhone(pdto.getPhone());
            loginMember.setEmail(pdto.getEmail());
            loginMember.setZipCode(pdto.getZipCode());
            loginMember.setAddress(pdto.getAddress());
            loginMember.setAddress2(pdto.getAddress2());
            loginMember.setSmsReceiveYN(pdto.getSmsReceiveYN());
            loginMember.setEmailReceiveYN(pdto.getEmailReceiveYN());
            
            session.setAttribute("loginMember", loginMember);
            rttr.addFlashAttribute("msg", "프로필 정보가 성공적으로 수정되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "프로필 정보 수정에 실패했습니다.");
        }

        return "redirect:/profile";
    }

    @GetMapping("/withdraw")
    public String withDrawForm(HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/member/login";
        }
        return "profile/withdraw";
    }

    @PostMapping("/withdraw")
    public String withDraw(@RequestParam("pass") String pass, SessionStatus status, HttpSession session, RedirectAttributes rttr) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        if (ps.withDraw(String.valueOf(loginMember.getMemberCode()), pass)) {
            status.setComplete();
            session.invalidate();
            rttr.addFlashAttribute("msg", "회원탈퇴가 완료되었습니다.");
            return "redirect:/";
        } else {
            rttr.addFlashAttribute("msg", "비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/withdraw";
        }
    }
}