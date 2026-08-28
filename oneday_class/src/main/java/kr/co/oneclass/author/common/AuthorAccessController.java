package kr.co.oneclass.author.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.profile.AuthorProfileDTO;
import kr.co.oneclass.author.profile.AuthorService;
import kr.co.oneclass.member.Member;

@Controller
public class AuthorAccessController {

	private final AuthorSessionService authorSessionService;
	private final AuthorService authorService;

	public AuthorAccessController(AuthorSessionService authorSessionService, AuthorService authorService) {
		this.authorSessionService = authorSessionService;
		this.authorService = authorService;
	}

	@GetMapping("/author/access")
    public String accessGuide(HttpSession session) {
		Member loginMember = loginMember(session);
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		AuthorSessionDTO author = authorSessionService.getAuthorByMemberCode(loginMember.getMemberCode());
		if (author == null || !author.isProfileComplete()) {
			return "redirect:/author/access/profile";
		}
		authorSessionService.activateCompletedAuthor(loginMember.getMemberCode());
		return "redirect:/author";
    }

    @GetMapping("/author/start")
    public String startAuthor(HttpSession session) {
        Member loginMember = loginMember(session);
        if (loginMember == null) {
            return "redirect:/member/login";
        }

		AuthorSessionDTO author = authorSessionService.getAuthorByMemberCode(loginMember.getMemberCode());
		if (author == null || !author.isProfileComplete()) {
			return "redirect:/author/access/profile";
		}
		authorSessionService.activateCompletedAuthor(loginMember.getMemberCode());
		return "redirect:/author";
    }

	@GetMapping("/author/access/profile")
	public String authorProfileForm(Model model, HttpSession session) {
		Member loginMember = loginMember(session);
		if (loginMember == null) {
			return "redirect:/member/login";
		}

		AuthorSessionDTO author = authorSessionService.getAuthorByMemberCode(loginMember.getMemberCode());
		if (author != null && author.isProfileComplete()) {
			authorSessionService.activateCompletedAuthor(loginMember.getMemberCode());
			return "redirect:/author";
		}

		AuthorProfileDTO profile = author == null
				? new AuthorProfileDTO()
				: authorService.getAuthorProfile(author.getAuthorCode());
		profile.setAuthorName(loginMember.getName());
		profile.setEmail(loginMember.getEmail());
		model.addAttribute("profile", profile);
		return "author/access-profile";
	}

	@PostMapping("/author/access/profile")
	public String submitAuthorProfile(
			AuthorProfileDTO profile,
			@RequestParam(value = "profileFile", required = false) MultipartFile profileFile,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		Member loginMember = loginMember(session);
		if (loginMember == null) {
			return "redirect:/member/login";
		}

		AuthorSessionDTO author = authorSessionService.getAuthorByMemberCode(loginMember.getMemberCode());
		if (author != null && author.isProfileComplete()) {
			authorSessionService.activateCompletedAuthor(loginMember.getMemberCode());
			return "redirect:/author";
		}

		try {
			boolean saved;
			if (author == null) {
				saved = authorService.registerAuthorProfile(
						loginMember.getMemberCode(), profile, profileFile);
			} else {
				profile.setAuthorCode(author.getAuthorCode());
				saved = authorService.modifyAuthorProfile(profile, profileFile);
			}
			if (!saved) {
				throw new IllegalStateException("작가 프로필을 저장하지 못했습니다.");
			}
			authorSessionService.activateCompletedAuthor(loginMember.getMemberCode());
			return "redirect:/author";
		} catch (IllegalArgumentException | IllegalStateException exception) {
			redirectAttributes.addFlashAttribute("profileError", exception.getMessage());
			return "redirect:/author/access/profile";
		}
	}

	private Member loginMember(HttpSession session) {
		return session == null ? null : (Member) session.getAttribute("loginMember");
	}
}
