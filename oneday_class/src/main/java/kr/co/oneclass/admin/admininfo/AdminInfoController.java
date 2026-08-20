package kr.co.oneclass.admin.admininfo;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.oneclass.admin.login.AdminUserDetails;

@Controller
@RequestMapping("/admin/info")
public class AdminInfoController {

	private static final String VERIFIED_MANAGER_CODE = "adminInfoVerifiedManagerCode";

	private final AdminInfoService adminInfoService;

	public AdminInfoController(AdminInfoService adminInfoService) {

		this.adminInfoService = adminInfoService;
	}

	@GetMapping("/verify")
	public String showPasswordVerifyPage(@AuthenticationPrincipal AdminUserDetails loginAdmin, Model model) {

		model.addAttribute("adminId", loginAdmin.toDomain().getId());

		model.addAttribute("verifyDTO", new AdminPasswordVerifyDTO());

		return "admin/admininfo/passwordVerify";
	}

	@PostMapping("/verify")
	public String verifyPassword(@AuthenticationPrincipal AdminUserDetails loginAdmin,
			@ModelAttribute("verifyDTO") AdminPasswordVerifyDTO verifyDTO, HttpSession session,
			RedirectAttributes redirectAttributes) {

		int managerCode = loginAdmin.toDomain().getManagerCode();

		boolean verified = adminInfoService.verifyPassword(managerCode, verifyDTO);

		if (!verified) {

			redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");

			return "redirect:/admin/info/verify";
		}

		session.setAttribute(AdminInfoVerifyInterceptor.VERIFIED_MANAGER_CODE, managerCode);

		return "redirect:/admin/info/edit";
	}

	@GetMapping("/edit")
	public String showAdminInfo(@AuthenticationPrincipal AdminUserDetails loginAdmin, HttpSession session,
			Model model) {

		int managerCode = loginAdmin.toDomain().getManagerCode();

		if (!isVerified(session, managerCode)) {

			return "redirect:/admin/info/verify";
		}

		AdminInfoDomain adminInfo = adminInfoService.getAdminInfo(managerCode);

		if (adminInfo == null) {
			return "redirect:/admin/dashboard";
		}

		AdminInfoUpdateDTO updateDTO = new AdminInfoUpdateDTO();

		updateDTO.setName(adminInfo.getName());

		updateDTO.setEmail(adminInfo.getEmail());

		model.addAttribute("adminInfo", adminInfo);

		model.addAttribute("updateDTO", updateDTO);

		return "admin/admininfo/adminInfoEdit";
	}

	@PostMapping("/edit")
	public String updateAdminInfo(@AuthenticationPrincipal AdminUserDetails loginAdmin,
			@ModelAttribute AdminInfoUpdateDTO updateDTO, HttpSession session, RedirectAttributes redirectAttributes) {

		int managerCode = loginAdmin.toDomain().getManagerCode();

		boolean result = adminInfoService.updateAdminInfo(managerCode, updateDTO);

		if (!result) {
			redirectAttributes.addFlashAttribute("error", "관리자 정보 수정에 실패했습니다.");

			return "redirect:/admin/info/edit";
		}

		// 비밀번호 재확인 인증 상태 제거
		session.removeAttribute(AdminInfoVerifyInterceptor.VERIFIED_MANAGER_CODE);

		redirectAttributes.addFlashAttribute("message", "관리자 정보가 수정되었습니다.");

		return "redirect:/admin/dashboard";
	}

	private boolean isVerified(HttpSession session, int managerCode) {

		Object verifiedCode = session.getAttribute(VERIFIED_MANAGER_CODE);

		return verifiedCode instanceof Integer && ((Integer) verifiedCode) == managerCode;
	}
}
