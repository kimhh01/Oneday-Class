package kr.co.oneclass.author.profile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.util.AuthorSessionUtils;
import kr.co.oneclass.author.profile.dto.AuthorProfileDTO;
import kr.co.oneclass.author.profile.service.AuthorService;
import kr.co.oneclass.author.settlement.service.SettlementService;

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
            HttpSession session) {

        apDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        aService.modifyAuthorProfile(apDTO, profileFile);
        return "redirect:/author/profile";
    }
}
