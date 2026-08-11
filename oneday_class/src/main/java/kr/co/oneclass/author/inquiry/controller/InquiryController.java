package kr.co.oneclass.author.inquiry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.util.AuthorSessionUtils;
import kr.co.oneclass.author.inquiry.dto.InquiryFormDTO;
import kr.co.oneclass.author.inquiry.dto.InquirySearchDTO;
import kr.co.oneclass.author.inquiry.service.InquiryService;

@Controller
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping("/author/qna")
    public String inquiryList(InquirySearchDTO searchDTO, Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        searchDTO.setAuthorCode(authorCode);
        searchDTO.setKeyword(trimToNull(searchDTO.getKeyword()));
        if (searchDTO.getAnswerStatus() == null) {
            searchDTO.setAnswerStatus("all");
        }

        model.addAttribute("summary", inquiryService.getInquirySummary(authorCode));
        model.addAttribute("types", inquiryService.getInquiryTypeList());
        model.addAttribute("inquiries", inquiryService.getInquiryList(searchDTO));
        model.addAttribute("search", searchDTO);
        return "author/qna";
    }

    @GetMapping("/author/qna/new")
    public String inquiryForm(Model model, HttpSession session) {
        if (!model.containsAttribute("inquiryForm")) {
            model.addAttribute("inquiryForm", new InquiryFormDTO());
        }
        model.addAttribute("types", inquiryService.getInquiryTypeList());
        return "author/qna-form";
    }

    @PostMapping("/author/qna")
    public String addInquiry(
            InquiryFormDTO formDTO,
            @RequestParam(value = "inquiryFile", required = false) MultipartFile inquiryFile,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        formDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            int inquiryCode = inquiryService.addInquiry(formDTO, inquiryFile);
            redirectAttributes.addFlashAttribute("message", "1:1 문의가 접수되었습니다.");
            return "redirect:/author/qna/" + inquiryCode;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("inquiryForm", formDTO);
            model.addAttribute("types", inquiryService.getInquiryTypeList());
            return "author/qna-form";
        }
    }

    @GetMapping("/author/qna/{inquiryCode}")
    public String inquiryDetail(@PathVariable("inquiryCode") int inquiryCode,
            Model model, HttpSession session) {
        var inquiry = inquiryService.getInquiryDetail(AuthorSessionUtils.getAuthorCode(session), inquiryCode);
        if (inquiry == null) {
            return "redirect:/author/qna";
        }
        model.addAttribute("inquiry", inquiry);
        return "author/qna-detail";
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
