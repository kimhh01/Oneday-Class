package kr.co.oneclass.admin.inquiry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/inquiries")
public class AdminInquiryController {
	private final AdminInquiryService inquiryService;

	public AdminInquiryController(AdminInquiryService inquiryService) {
		this.inquiryService = inquiryService;
	}

	@GetMapping
	public String showInquiryList(@ModelAttribute("searchDTO") AdminInquirySearchDTO searchDTO, Model model) {
		model.addAttribute("statistics", inquiryService.getInquiryStatistics());
		model.addAttribute("inquiries", inquiryService.getInquiryList(searchDTO));
		model.addAttribute("page", inquiryService.getPage(searchDTO));
		model.addAttribute("inquiryTypes", inquiryService.getInquiryTypeList());
		return "admin/inquiry/inquiryList";
	}

	@GetMapping("/{inquiryCode}")
	public String showInquiryDetail(@PathVariable int inquiryCode, Model model) {
		AdminInquiryDetailDomain inquiry = inquiryService.getInquiryDetail(inquiryCode);
		if (inquiry == null)
			return "redirect:/admin/inquiries";
		model.addAttribute("inquiry", inquiry);
		return "admin/inquiry/inquiryDetail";
	}

	@PostMapping("/{inquiryCode}/answer")
	public String registerInquiryAnswer(@PathVariable int inquiryCode, @RequestParam String answer,
			RedirectAttributes redirectAttributes) {
		AdminInquiryAnswerDTO dto = new AdminInquiryAnswerDTO();
		dto.setInquiryCode(inquiryCode);
		dto.setAnswer(answer);
		boolean result = inquiryService.registerInquiryAnswer(dto);
		redirectAttributes.addFlashAttribute("message", result ? "답변이 등록되었습니다." : "답변 등록에 실패했습니다.");
		return "redirect:/admin/inquiries/" + inquiryCode;
	}
}
