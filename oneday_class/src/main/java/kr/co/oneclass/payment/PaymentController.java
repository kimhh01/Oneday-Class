package kr.co.oneclass.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;
import kr.co.oneclass.member.Member; // 💡 Member 클래스 import 추가
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping 
    public String payment(@RequestParam("scheduleCode") int scheduleCode, HttpSession session, Model model) {
        // 💡 로그인 여부 서버 검증 추가
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login/general";
        }

        ClassDTO classDTO = paymentService.getClassDetailByScheduleCode(scheduleCode);
        ScheduleDTO schedule = paymentService.getClassDetailByScheduleCode2(scheduleCode);
        CategoryDTO category = paymentService.getCategory(scheduleCode);
        
        model.addAttribute("scheduleCode", scheduleCode);
        model.addAttribute("classInfo", classDTO);
        model.addAttribute("schedule", schedule);
        model.addAttribute("category", category);
        
        return "payment/payment";
    }

    @GetMapping("/paymentComplete")
    public String paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam("scheduleCode") int scheduleCode,
            @RequestParam(defaultValue = "1") int peopleCount,
            @RequestParam(defaultValue = "CARD") String paymentMethod,
            @RequestParam(value = "memberCode", required = false, defaultValue = "0") int memberCodeParam, // 💡 추가
            HttpSession session, 
            Model model) {

        // 1. 화면 표출용 데이터 사전 조회
        ClassDTO classDTO = null;
        ScheduleDTO schedule = null;
        CategoryDTO category = null;

        if (scheduleCode > 0) {
            classDTO = paymentService.getClassDetailByScheduleCode(scheduleCode);
            schedule = paymentService.getClassDetailByScheduleCode2(scheduleCode);
            category = paymentService.getCategory(scheduleCode);
        }

        if (classDTO == null) classDTO = new ClassDTO();
        if (schedule == null) schedule = new ScheduleDTO();
        if (category == null) category = new CategoryDTO();

        // 2. 세션에서 로그인 유저 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");
        int memberCode = 0;

        if (loginMember != null) {
            memberCode = loginMember.getMemberCode();
        } else if (memberCodeParam > 0) {
            // PG사 리다이렉트로 세션이 잠시 유실되었을 경우 URL 파라미터값 활용
            memberCode = memberCodeParam;
        } else {
            // 회원 정보를 전혀 찾을 수 없는 경우에만 이동
            return "redirect:/member/login";
        }

        int classCode = classDTO.getClassCode();

        // 3. 결제 승인 및 DB 저장 처리
        try {
            paymentService.confirmPayment(paymentKey, orderId, amount, 
                    scheduleCode, peopleCount, paymentMethod, 
                    memberCode, classCode);
        } catch (Exception e) {
            e.printStackTrace(); 
        }

        model.addAttribute("classInfo", classDTO);
        model.addAttribute("schedule", schedule);
        model.addAttribute("category", category);

        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        model.addAttribute("peopleCount", peopleCount);

        return "payment/paymentComplete";
    }

    @GetMapping("/paymentFail")
    public String paymentFail(@RequestParam(value = "code", required = false) String code,
                              @RequestParam(value = "message", required = false) String message,
                              Model model) {
        
        model.addAttribute("code", code);
        model.addAttribute("message", message);

        return "payment/paymentFail"; 
    }
}