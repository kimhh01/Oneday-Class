package kr.co.oneclass.author.profile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.profile.dto.AuthorProfileDTO;
import kr.co.oneclass.author.profile.service.AuthorService;
import kr.co.oneclass.author.settlement.service.SettlementService;

@Controller
public class AuthorProfileController {

    // TODO: 로그인 세션 연결 후 제거 - 세션에서 작가 코드를 꺼내도록 교체
    // CREATOR.OPERATOR_CODE 실제값. int 범위를 넘는 코드가 있어 long 이다
    private static final long SAMPLE_AUTHOR_CODE = 1010101010L;

    private final AuthorService aService;
    private final SettlementService sService;

    public AuthorProfileController(AuthorService aService, SettlementService sService) {
        this.aService = aService;
        this.sService = sService;
    }

    // 작가 프로필 조회 및 수정 화면
    @GetMapping("/author/profile")
    public String authorProfile(Model model, HttpSession session) {
        model.addAttribute("profile", aService.getAuthorProfile(SAMPLE_AUTHOR_CODE));
        model.addAttribute("account", sService.getSettlementAccount(SAMPLE_AUTHOR_CODE));
        return "author/profile";
    }

    // 작가 프로필 정보와 프로필 이미지를 수정한다
    @PostMapping("/author/profile")
    public String modifyAuthorProfile(
            AuthorProfileDTO apDTO,
            @RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
            HttpSession session) {

        // TODO: 로그인 세션 연결 후 제거 - 세션에서 작가 코드를 꺼내도록 교체
        apDTO.setAuthorCode(SAMPLE_AUTHOR_CODE);
        aService.modifyAuthorProfile(apDTO, profileFile);
        return "redirect:/author/profile";
    }
}
