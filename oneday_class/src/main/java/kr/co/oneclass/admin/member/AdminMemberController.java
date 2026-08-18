package kr.co.oneclass.admin.member;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.oneclass.admin.common.PageDomain;

@Controller
@RequestMapping("/admin")
public class AdminMemberController {

	private final AdminMemberService adminMemberService;

	public AdminMemberController(AdminMemberService adminMemberService) {
		this.adminMemberService = adminMemberService;
	}

	@GetMapping("/members")
	public String memberList(AdminMemberSearchDTO searchDTO, Model model) {

		List<AdminMemberDomain> members = adminMemberService.getMemberList(searchDTO);

		PageDomain page = adminMemberService.getPage(searchDTO);

		model.addAttribute("members", members);
		model.addAttribute("page", page);
		model.addAttribute("searchDTO", searchDTO);

		return "admin/member/memberList";
	}

	@GetMapping("/members/{memberCode}")
	public String memberDetail(@PathVariable int memberCode,
			@RequestParam(name = "reservationPage", defaultValue = "1") int reservationPage, Model model) {

		AdminMemberDomain member = adminMemberService.getMemberDetail(memberCode);

		List<AdminMemberReservationDomain> reservations = adminMemberService.getMemberReservationList(memberCode,
				reservationPage);

		PageDomain reservationPageDomain = adminMemberService.getMemberReservationPage(memberCode, reservationPage);

		model.addAttribute("member", member);
		model.addAttribute("reservations", reservations);
		model.addAttribute("reservationPage", reservationPageDomain);

		return "admin/member/memberDetail";
	}

}