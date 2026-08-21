package kr.co.oneclass.author.profile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.AuthorSessionUtils;
import kr.co.oneclass.author.settlement.SettlementService;

@Controller
public class AuthorProfileController {

    private final AuthorService aService;
    private final SettlementService sService;

    public AuthorProfileController(AuthorService aService, SettlementService sService) {
        this.aService = aService;
        this.sService = sService;
    }

    // 작가 프로필 조회 및 수정 화면
    @GetMapping("/author/profile")
    public String authorProfile(Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        model.addAttribute("profile", aService.getAuthorProfile(authorCode));
        model.addAttribute("account", sService.getSettlementAccount(authorCode));
        return "author/profile";
    }

    // 작가 프로필 정보와 프로필 이미지를 수정한다
    @PostMapping("/author/profile")
    public String modifyAuthorProfile(
            AuthorProfileDTO apDTO,
            @RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        apDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            if (aService.modifyAuthorProfile(apDTO, profileFile)) {
                redirectAttributes.addFlashAttribute("profileMessage", "프로필 정보를 저장했습니다.");
            } else {
                redirectAttributes.addFlashAttribute("profileError", "프로필 정보를 저장하지 못했습니다.");
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("profileError", exception.getMessage());
        }
        return "redirect:/author/profile";
    }
}
