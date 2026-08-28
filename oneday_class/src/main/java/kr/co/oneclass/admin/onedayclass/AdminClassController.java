package kr.co.oneclass.admin.onedayclass;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/onedayclass")
public class AdminClassController {

	private final AdminClassService classService;

	public AdminClassController(AdminClassService classService) {

		this.classService = classService;
	}

	@GetMapping
	public String showClassList(@ModelAttribute("searchDTO") AdminClassSearchDTO searchDTO, Model model) {

		model.addAttribute("classes", classService.getClassList(searchDTO));

		model.addAttribute("classCount", classService.getClassCount(searchDTO));

		model.addAttribute("page", classService.getPage(searchDTO));

		return "admin/onedayclass/classList";
	}

	@GetMapping("/{classCode}")
	public String showClassDetail(@PathVariable int classCode, Model model) {

		model.addAttribute("clazz", classService.getClassDetail(classCode));

		model.addAttribute("images", classService.getClassImageList(classCode));

		model.addAttribute("tags", classService.getClassTagList(classCode));

		model.addAttribute("schedules", classService.getClassScheduleList(classCode));

		model.addAttribute("curriculums", classService.getClassCurriculumList(classCode));

		model.addAttribute("materials", classService.getClassMaterialList(classCode));

		model.addAttribute("offerings", classService.getClassOfferingList(classCode));

		model.addAttribute("additionalInfos", classService.getClassAdditionalInfoList(classCode));

		model.addAttribute("finishedProducts", classService.getFinishedProductList(classCode));

		return "admin/onedayclass/classDetail";
	}

	// 판매 중지
	@PostMapping("/{classCode}/status")
	public String changeClassStatus(@PathVariable int classCode) {

		classService.updateClassStatus(classCode);

		return "redirect:/admin/onedayclass/" + classCode;
	}

	// 클래스 승인
	@PostMapping("/{classCode}/approve")
	public String approveClass(@PathVariable int classCode) {

		classService.approveClass(classCode);

		return "redirect:/admin/onedayclass/" + classCode;
	}

	// 클래스 반려
	@PostMapping("/{classCode}/reject")
	public String rejectClass(@PathVariable int classCode, AdminClassReviewDTO dto) {

		classService.rejectClass(classCode, dto);

		return "redirect:/admin/onedayclass/" + classCode;
	}
}