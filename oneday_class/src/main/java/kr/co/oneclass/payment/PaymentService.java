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
    
    // application.properties의 토스 시크릿키를 주입받음 (기본값 설정)
    @Value("${toss.secret-key}")
    private String secretKey;

    // 생성자 주입
    public PaymentService(ReservationDAO rDAO, PaymentDAO pDAO) {
        this.rDAO = rDAO;
        this.pDAO = pDAO;
    }

    // 예약 정보 조회
    public ReservationDTO getReservationInfo(int reservationCode) {
        return rDAO.selectReservation(reservationCode);
    }

    // 인원수 변경
    public int updatePersonCount(int reservationCode, int count) {
        return rDAO.updatePersonCount(reservationCode, count);
    }

    // 총 금액 계산 및 DB 업데이트
    public int calculateTotalPrice(int reservationCode, int unitPrice) {
        ReservationDTO reservation = rDAO.selectReservation(reservationCode);
        int totalPrice = unitPrice * reservation.getPeopleNumber();
        return rDAO.updateTotalPrice(reservationCode, totalPrice);
    }

    // 결제 추가
    @Transactional
    public int addPayment(PaymentDTO pDTO) {
        return pDAO.insertPayment(pDTO);
    }

    // 결제 상태 변경
    public int modifyPaymentStatus(int paymentCode, String status) {
        return pDAO.updatePaymentStatus(paymentCode, status);
    }

    // 결제 내역 조회
    public PaymentDTO getPayment(int paymentCode) {
        return pDAO.selectPayment(paymentCode);
    }

    // 주문 정보
    public ClassDTO getClassDetailByScheduleCode(int scheduleCode) {
        return rDAO.selectClassDetailByScheduleCode(scheduleCode);
    }

    // 주문 정보2
    public ScheduleDTO getClassDetailByScheduleCode2(int scheduleCode) {
        return rDAO.selectClassDetailByScheduleCode2(scheduleCode);
    }
    
    public CategoryDTO getCategory(int scheduleCode) {
        return rDAO.selectCategory(scheduleCode);
    }
    
    @Transactional
    public void confirmPayment(String paymentKey, String orderId, Long amount, 
                               int scheduleCode, int peopleCount, String paymentMethod) {
        
        // 1. 토스 페이먼츠 승인 API 호출
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
            // 승인 API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    entity,
                    Map.class
            );
            
            // 2. 승인 성공 시 DB 저장 (신규 결제 처리)
            int reservationCode = 1; // 실제 예약 번호
            PaymentDTO pDTO = new PaymentDTO();
            pDTO.setReservationCode(reservationCode); 
            pDTO.setAmount(amount.intValue()); 
            pDTO.setMeans(paymentMethod); 
            pDTO.setPaymentDate(new java.sql.Date(System.currentTimeMillis())); 
            pDTO.setStatus("COMPLETED"); 
            pDTO.setPgCode(paymentKey); 
            pDTO.setRefund(0);       
            pDTO.setRefundDate(null); 

            addPayment(pDTO);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // 이미 처리된 결제(ALREADY_PROCESSED_PAYMENT) 에러인 경우 예외를 삼키고 정상 진행
            if (e.getResponseBodyAsString().contains("ALREADY_PROCESSED_PAYMENT")) {
                System.out.println("이미 승인 완료된 결제 건입니다. (새로고침 대응)");
                return;
            }
            // 다른 진짜 에러(잔액부족, 금액불일치 등)는 그대로 던지기
            throw e; 
        }
    }
}