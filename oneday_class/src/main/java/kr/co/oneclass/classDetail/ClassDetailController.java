package kr.co.oneclass.classDetail;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.member.Member;

@Controller
public class ClassDetailController {

    @Autowired
    private ClassDetailService cdService;

    @GetMapping("/classDetail")
    public String useClass(@RequestParam("classCode") String classCode, Model model, HttpSession session) {
        // 서비스에서 모든 조립 과정을 마친 하나의 객체를 가져옵니다.
    	int code=Integer.parseInt(classCode);
        ClassDetailResponseDTO detail = cdService.getClassDetail(code);
        
        
        
        if (detail == null) {
        	return "redirect:/"; 
            //return "error/404"; // 데이터가 없을 때의 예외 처리
        }

        // 모델에 단 하나의 attribute만 추가하므로 코드가 간결해집니다.
        model.addAttribute("detail", detail);

        return "classDetail/classDetail";
    }
    
    
 // 💡 상세페이지 전용 찜하기 토글 URL
    @PostMapping("/classDetail/bookmark/toggle")
    @ResponseBody
    public String toggleBookmarkDetail(
            @RequestParam("classCode") String classCode,
            HttpSession session) {

        // 세션에서 로그인한 회원 확인
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "LOGIN_REQUIRED";
        }

        String memberCode = String.valueOf(loginMember.getMemberCode());

        // 💡 ClassDetailService의 addBookmark 메소드 실행
        boolean isAdded = cdService.addBookmark(memberCode, classCode);

        // 추가되었으면 ADDED, 삭제되었으면 REMOVED 반환
        return isAdded ? "ADDED" : "REMOVED";
    }
    
    //환불규정
    @GetMapping("/refundPolicy")
    public String refundPolicy() {
    	return "/refundPolicy/refundPolicy";
    }
}
