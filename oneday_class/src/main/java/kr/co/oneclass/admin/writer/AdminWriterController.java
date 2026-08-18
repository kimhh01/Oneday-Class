package kr.co.oneclass.admin.writer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/writer")
public class AdminWriterController {

	private AdminWriterService writerService;

	public AdminWriterController(AdminWriterService writerService) {

		this.writerService = writerService;
	}

	@GetMapping
	public String showWriterList(@ModelAttribute("searchDTO") AdminWriterSearchDTO searchDTO, Model model) {

		model.addAttribute("writers", writerService.getWriterList(searchDTO));

		model.addAttribute("writerCount", writerService.getWriterCount(searchDTO));

		return "admin/writer/writerList";
	}

	@GetMapping("/{writerCode}")
	public String showWriterDetail(@PathVariable long writerCode, Model model) {

		model.addAttribute("writer", writerService.getWriterDetail(writerCode));

		model.addAttribute("statistics", writerService.getWriterStatistics(writerCode));

		model.addAttribute("classes", writerService.getWriterClassList(writerCode));

		return "admin/writer/writerDetail";
	}
}
