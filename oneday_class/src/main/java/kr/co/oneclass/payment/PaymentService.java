package kr.co.oneclass.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;

@Service
public class PaymentService {

    private final ReservationDAO rDAO;
    private final PaymentDAO pDAO;
    
    @Value("${toss.secret-key}")
    private String secretKey;

    public PaymentService(ReservationDAO rDAO, PaymentDAO pDAO) {
        this.rDAO = rDAO;
        this.pDAO = pDAO;
    }

    // ==========================================
    // 1. 결제 페이지 화면 출력용 메서드 (3개)
    // ==========================================
    
    public ClassDTO getClassDetailByScheduleCode(int scheduleCode) {
        return rDAO.selectClassDetailByScheduleCode(scheduleCode);
    }

    public ScheduleDTO getClassDetailByScheduleCode2(int scheduleCode) {
        return rDAO.selectClassDetailByScheduleCode2(scheduleCode);
    }

    public CategoryDTO getCategory(int scheduleCode) {
        return rDAO.selectCategory(scheduleCode);
    }

    // ==========================================
    // 2. 실제 예매 및 결제 승인 처리 메서드
    // ==========================================
    
    @Transactional
    public void confirmPayment(String paymentKey, String orderId, Long amount, 
                               int scheduleCode, int peopleCount, String paymentMethod, 
                               int memberCode, int classCode) {
        
        // ① 토스페이먼츠 승인 API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        String authorization = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + authorization);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("paymentKey", paymentKey);
        requestBody.put("orderId", orderId);
        requestBody.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    entity,
                    Map.class
            );
            
            // ② 예약(reservation) DB 저장
            ReservationDTO rDTO = new ReservationDTO();
            rDTO.setClassCode(classCode);
            rDTO.setMemberCode(memberCode);
            rDTO.setScheduleCode(scheduleCode);
            rDTO.setPeopleNumber(peopleCount);
            rDTO.setTotalPrice(amount.intValue());
            rDTO.setStatus("예약");
            
            rDAO.insertReservation(rDTO); // XML에 selectKey가 있다면 rDTO.getReservationCode()에 PK가 담김

            // ③ 결제(payment) DB 저장
            PaymentDTO pDTO = new PaymentDTO();
            pDTO.setReservationCode(rDTO.getReservationCode()); 
            pDTO.setAmount(amount.intValue()); 
            pDTO.setMeans(paymentMethod); 
            pDTO.setPaymentDate(new java.sql.Date(System.currentTimeMillis())); 
            pDTO.setStatus("COMPLETED"); 
            pDTO.setPgCode(paymentKey); 

            pDAO.insertPayment(pDTO);

            // ④ 좌석 수 차감 (rDAO의 updateRemaining 직접 호출)
            int updatedRows = rDAO.updateRemaining(scheduleCode, peopleCount);
            
            // (선택 사항) 만약 차감된 행이 0개라면 수량 부족으로 예외 발생 -> 트랜잭션 롤백
            if (updatedRows == 0) {
                throw new RuntimeException("잔여 좌석이 부족하여 예약을 완료할 수 없습니다.");
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            if (e.getResponseBodyAsString().contains("ALREADY_PROCESSED_PAYMENT")) {
                System.out.println("이미 승인 완료된 결제 건입니다. (새로고침 대응)");
                return;
            }
            throw e; 
        }
    }
    

}