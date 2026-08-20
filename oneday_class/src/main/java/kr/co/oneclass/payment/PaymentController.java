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
            HttpSession session, // 로그인 유저 정보를 세션에서 가져오기 위해 추가
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

        // 2. memberCode & classCode 추출
        // TODO: 실제 세션에 저장된 로그인 유저 DTO 객체 및 필드명에 맞게 수정해주세요.
        int memberCode = 1; 
        /* 
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        if (loginUser != null) {
            memberCode = loginUser.getMemberCode();
        } 
        */
        
        int classCode = classDTO.getClassCode(); // 조회한 클래스 정보에서 classCode 추출

        try {
            paymentService.confirmPayment(paymentKey, orderId, amount, 
                    scheduleCode, peopleCount, paymentMethod, 
                    memberCode, classCode);
        } catch (Exception e) {
            // 예외 원인을 상세히 보기 위해 printStackTrace() 출력
            e.printStackTrace(); 
        }

        // 4. Model 데이터 전달
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