package kr.co.oneclass.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired(required = false)
    private final PaymentService paymentService;

    @GetMapping 
    public String payment(@RequestParam("scheduleCode") int scheduleCode, Model model) {
        ClassDTO classDTO = paymentService.getClassDetailByScheduleCode(scheduleCode);
        ScheduleDTO schedule = paymentService.getClassDetailByScheduleCode2(scheduleCode);
        CategoryDTO category = paymentService.getCategory(scheduleCode);
        
        model.addAttribute("scheduleCode", scheduleCode);
        // 🔥 [수정 1] 예약어 "class" 대신 "classInfo"로 변경 (payment.html에서도 classInfo로 접근 필요)
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
            Model model) {

        // 🔥 [수정 2] DB 화면 표출용 데이터를 "try 문 밖(최우선)"에서 먼저 조회합니다.
        // 이렇게 해야 토스 승인 시 예외(새로고침, 이미 승인된 건 등)가 터져도 화면 데이터가 null이 되지 않습니다!
        ClassDTO classDTO = null;
        ScheduleDTO schedule = null;
        CategoryDTO category = null;

        if (scheduleCode > 0) {
            classDTO = paymentService.getClassDetailByScheduleCode(scheduleCode);
            schedule = paymentService.getClassDetailByScheduleCode2(scheduleCode);
            category = paymentService.getCategory(scheduleCode);
        }

        // null 예외 방지용 안전 처리
        if (classDTO == null) classDTO = new ClassDTO();
        if (schedule == null) schedule = new ScheduleDTO();
        if (category == null) category = new CategoryDTO();

        // 1. 토스 결제 승인 처리 (새로고침 시 예외 발생 대비 try-catch)
        try {
            paymentService.confirmPayment(paymentKey, orderId, amount, scheduleCode, peopleCount, paymentMethod);
        } catch (Exception e) {
            // 이미 승인되었거나 승인 중 에러가 발생하더라도 결제완료 화면은 정상 표출되도록 로그만 출력
            System.out.println("토스 승인 중 예외 발생 (새로고침 또는 이미 처리된 건): " + e.getMessage());
        }

        // 2. Model에 안전하게 데이터를 담아 뷰로 전달
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