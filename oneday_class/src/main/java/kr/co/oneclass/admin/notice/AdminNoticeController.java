package kr.co.oneclass.admin.notice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import kr.co.oneclass.admin.login.AdminUserDetails;

@Controller
@RequestMapping("/admin/notices")
public class AdminNoticeController {
	private final AdminNoticeService service;

	public AdminNoticeController(AdminNoticeService service) {
		this.service = service;
	}

	@GetMapping
	public String list(@ModelAttribute("searchDTO") AdminNoticeSearchDTO s, Model m) {
		m.addAttribute("notices", service.getNoticeList(s));
		m.addAttribute("page", service.getNoticePage(s));
		return "admin/notice/noticeList";
	}

	@GetMapping("/{noticeCode}")
	public String detail(@PathVariable int noticeCode, Model m) {
		AdminNoticeDetailDomain n = service.getNoticeDetail(noticeCode);
		if (n == null)
			return "redirect:/admin/notices";
		m.addAttribute("notice", n);
		return "admin/notice/noticeDetail";
	}

	@GetMapping("/register")
	public String registerForm(Model m) {
		m.addAttribute("noticeDTO", new AdminNoticeCreateDTO());
		return "admin/notice/noticeRegister";
	}

	@PostMapping
	public String register(@AuthenticationPrincipal AdminUserDetails admin,
			@ModelAttribute("noticeDTO") AdminNoticeCreateDTO d, RedirectAttributes ra) {
		// AdminUserDetails의 실제 getter명이 다르면 이 한 줄만 변경
		d.setManagerCode(admin.getManagerCode());
		ra.addFlashAttribute("message", service.registerNotice(d) ? "공지사항이 등록되었습니다." : "공지사항 등록에 실패했습니다.");
		return "redirect:/admin/notices";
	}

	@GetMapping("/{noticeCode}/edit")
	public String editForm(@PathVariable int noticeCode, Model m) {
		AdminNoticeDetailDomain n = service.getNoticeDetail(noticeCode);
		if (n == null)
			return "redirect:/admin/notices";
		AdminNoticeUpdateDTO d = new AdminNoticeUpdateDTO();
		d.setNoticeCode(n.getNoticeCode());
		d.setNoticeTitle(n.getNoticeTitle());
		d.setNoticeType(n.getNoticeType());
		d.setNoticeContent(n.getNoticeContent());
		d.setStatus(n.getStatus());
		m.addAttribute("noticeDTO", d);
		return "admin/notice/noticeUpdate";
	}

	@PostMapping("/{noticeCode}/edit")
	public String edit(@PathVariable int noticeCode, @ModelAttribute("noticeDTO") AdminNoticeUpdateDTO d,
			RedirectAttributes ra) {
		d.setNoticeCode(noticeCode);
		ra.addFlashAttribute("message", service.updateNotice(d) ? "공지사항이 수정되었습니다." : "공지사항 수정에 실패했습니다.");
		return "redirect:/admin/notices/" + noticeCode;
	}
}
