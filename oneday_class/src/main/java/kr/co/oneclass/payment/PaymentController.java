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
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping 
    public String payment(@RequestParam("scheduleCode") int scheduleCode, Model model) {
    	//HttpSession session
    	//Member loginUser = (Member) session.getAttribute("loginUser");
    	
        ClassDTO classDTO = paymentService.getClassDetailByScheduleCode(scheduleCode);
        ScheduleDTO schedule = paymentService.getClassDetailByScheduleCode2(scheduleCode);
        CategoryDTO category = paymentService.getCategory(scheduleCode);
        
        model.addAttribute("scheduleCode", scheduleCode);
        model.addAttribute("class", classDTO);
        model.addAttribute("schedule", schedule);
        model.addAttribute("category", category);
        
        return "payment/payment";
    }

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @RequestParam("scheduleCode") int scheduleCode,
            @RequestParam(defaultValue = "1") int peopleCount,
            @RequestParam(defaultValue = "CARD") String paymentMethod,
            Model model) {

        // 1. 토스 결제 승인 처리
        paymentService.confirmPayment(paymentKey, orderId, amount, scheduleCode, peopleCount, paymentMethod);

        // 2. ★ 성공 화면에 보여줄 클래스 정보 DB 조회 및 Model 추가!
        if (scheduleCode > 0) {
            ClassDTO classDTO = paymentService.getClassDetailByScheduleCode(scheduleCode);
            ScheduleDTO schedule = paymentService.getClassDetailByScheduleCode2(scheduleCode);
            CategoryDTO category = paymentService.getCategory(scheduleCode);

            model.addAttribute("class", classDTO);
            model.addAttribute("schedule", schedule);
            model.addAttribute("category", category);
        }

        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        model.addAttribute("peopleCount", peopleCount);

        return "payment/paymentComplete";
    }

    @GetMapping("/paymentFail") // 또는 JS에서 설정한 URL 경로
    public String paymentFail(@RequestParam(value = "code", required = false) String code,
                              @RequestParam(value = "message", required = false) String message,
                              Model model) {
        
        // 토스에서 넘어온 에러 코드 및 메시지를 뷰로 전달 (필요 시)
        model.addAttribute("code", code);
        model.addAttribute("message", message);

        // templates/payment/paymentFail.html 경로의 템플릿을 반환
        return "payment/paymentFail"; 
    }
}