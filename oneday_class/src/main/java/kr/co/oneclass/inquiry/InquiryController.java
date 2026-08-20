package kr.co.oneclass.inquiry;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 추가
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
public class InquiryController {

    @Autowired
    private InquiryService is;

    @Autowired
    private ProfileService ps;

    /**
     * 1. 문의 내역 목록 조회
     */
    @GetMapping("/inquiry")
    public String inquiryList(@RequestParam(value = "category", required = false, defaultValue = "ALL") String type,
                              Model model,
                              HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        int memberCode = loginMember.getMemberCode();

        Member member = ps.getProfile(String.valueOf(memberCode));
        model.addAttribute("member", member != null ? member : loginMember);

        List<InquiryDTO> typeList = is.getInquiryTypeList();
        model.addAttribute("typeList", typeList);

        List<InquiryDTO> inquiryList = is.getInquiryList(String.valueOf(memberCode), type);

        model.addAttribute("inquiryList", inquiryList);
        model.addAttribute("selectedCategory", type);

        return "inquiry/inquiry";
    }

    /**
     * 2. 문의 상세 조회
     */
    @GetMapping("/inquiry/detail")
    public String inquiryDetail(@RequestParam("inquiryCode") String inquiryCode,
                                Model model,
                                HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        int memberCode = loginMember.getMemberCode();

        Member member = ps.getProfile(String.valueOf(memberCode));
        model.addAttribute("member", member != null ? member : loginMember);

        InquiryDTO inquiry = is.getInquiryDetail(inquiryCode, String.valueOf(memberCode));
        model.addAttribute("inquiry", inquiry);

        return "inquiry/inquiry_detail";
    }

    /**
     * 3. 문의 작성 폼 이동
     */
    @GetMapping("/inquiry/write")
    public String inquiryForm(Model model, HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        Member member = ps.getProfile(String.valueOf(loginMember.getMemberCode()));
        model.addAttribute("member", member != null ? member : loginMember);

        List<InquiryDTO> typeList = is.getInquiryTypeList();
        model.addAttribute("typeList", typeList);

        return "inquiry/inquiry_write";
    }

    /**
     * 4. 신규 문의 등록 처리 (AJAX JSON 응답)
     */
    @PostMapping("/inquiry/write")
    @ResponseBody // 👈 핵심: JSON 형태로 데이터를 반환하도록 추가
    public Map<String, Object> writeInquiry(InquiryDTO idto,
                                           @RequestParam(value = "file", required = false) MultipartFile image,
                                           HttpSession session) {

        Map<String, Object> result = new HashMap<>();

        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("status", "LOGIN_REQUIRED");
            return result;
        }

        idto.setMemberCode(loginMember.getMemberCode());
        boolean isSuccess = is.writeInquiry(idto, image);

        if (isSuccess) {
            result.put("status", "SUCCESS");
            result.put("inquiryCode", idto.getInquiryCode()); // 생성된 PK 번호 전달
        } else {
            result.put("status", "FAIL");
        }

        return result;
    }
}