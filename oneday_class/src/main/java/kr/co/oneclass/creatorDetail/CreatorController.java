package kr.co.oneclass.creatorDetail;

import java.util.List;

import kr.co.oneclass.author.classbasic.ClassImageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.classDetail.OperatorDTO;
import kr.co.oneclass.classDetail.ReviewDTO;
import kr.co.oneclass.classDetail.ReviewSummaryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.member.Member;


@Controller
public class CreatorController {

	private final ClassImageDAO classImageDAO;
	@Autowired
	private CreatorService cs;

	CreatorController(ClassImageDAO classImageDAO) {
		this.classImageDAO = classImageDAO;
	}
	
	@GetMapping("/creatorDetail")
	public String creatorDetail(Model model, @RequestParam("creatorCode") long creatorCode) {
		
		OperatorDTO creator=cs.getCreator(creatorCode);
		List<ClassDTO> classList=cs.getClassList(creatorCode);
		List<ReviewDTO> reviewList=cs.getReviewList(creatorCode);
		ReviewSummaryDTO reivewSummary=cs.getReviewSummary(creatorCode);
		
		model.addAttribute("creator", creator);
		model.addAttribute("classList", classList);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewSummary", reivewSummary);
		
		return "creatorDetail/creatorDetail";
	}
	
}
