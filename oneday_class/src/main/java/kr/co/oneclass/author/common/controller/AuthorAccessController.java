package kr.co.oneclass.author.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorAccessController {

    @GetMapping("/author/access")
    public String accessGuide(
            @RequestParam(value = "reason", required = false) String reason,
            Model model) {
        boolean pending = "pending".equals(reason);
        model.addAttribute("accessTitle", pending ? "작가 승인 대기 중입니다" : "작가 등록이 필요합니다");
        model.addAttribute("accessMessage", pending
                ? "관리자 승인이 완료되면 작가 페이지를 이용할 수 있습니다."
                : "현재 계정에 연결된 작가 정보가 없습니다.");
        return "author/access-guide";
    }
}
