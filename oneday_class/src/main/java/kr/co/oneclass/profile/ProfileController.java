package kr.co.oneclass.profile;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.PassChangeDTO;
import kr.co.oneclass.profile.ProfileDTO;
import kr.co.oneclass.profile.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
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
            // [수정 위치] 원하는 기본 이미지 경로로 지정합니다.
            // 예시: "/images/default_profile.png" 또는 null (null 저장 시 View에서 기본 SVG 출력)
            String defaultPath = "/uploads/profile/default/default_profile.png"; // 원하는 경로 작성

            ps.changeProfileImg(String.valueOf(loginMember.getMemberCode()), defaultPath);
            loginMember.setProfileImg(defaultPath);
            session.setAttribute("loginMember", loginMember);
            
            rttr.addFlashAttribute("msg", "기본 프로필 이미지로 변경되었습니다.");
        } else if (image != null && !image.isEmpty()) {
            try {
                // 루트 디렉토리 내 uploads/profile/ 경로에 저장
                String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String savedFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File dest = new File(uploadDir + savedFilename);

                image.transferTo(dest);

                String imgPath = "/uploads/profile/" + savedFilename;
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
    public String changePassword(PassChangeDTO pdto, HttpSession session, RedirectAttributes rttr) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        pdto.setMemberCode(loginMember.getMemberCode());
        boolean isChanged = ps.changePassword(pdto);

        if (isChanged) {
            rttr.addFlashAttribute("msg", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            rttr.addFlashAttribute("msg", "비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해주세요.");
        }

        return "redirect:/profile";
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