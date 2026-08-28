package kr.co.oneclass.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseDAO rvd; // PurchaseDAO

    @Override
    public List<Purchase> getPurchaseList(String memberCode, String status) {
        return rvd.selectListByMember(memberCode, status);
    }

    @Override
    public Purchase getPurchaseDetail(String reservationCode) {
        return rvd.selectDetail(reservationCode);
    }

    /**
     * 구매/예약 및 결제 취소 처리
     * - 이미 날짜가 지난 '수강완료' 상태 및 '취소/환불' 상태 검증
     * - RESERVATION(상태: 취소) 및 PAYMENT(상태: 환불완료, REFUND=AMOUNT, REFUND_DATE=SYSDATE) 동시 수정
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> cancelPurchase(String reservationCode, int memberCode) {
        Map<String, Object> result = new HashMap<>();

        // 1. 상세 내역 사전 조회 (selectDetail 활용)
        Purchase detail = rvd.selectDetail(reservationCode);

        if (detail == null) {
            result.put("success", false);
            result.put("message", "존재하지 않는 예약 정보입니다.");
            return result;
        }

        String classStatus = detail.getClassStatus(); // Mapper의 CASE WHEN 처리 결과값

        // 2. [검증 1] 이미 취소/환불 처리된 예약인지 확인
        if (classStatus != null && (classStatus.contains("취소") || classStatus.contains("환불"))) {
            result.put("success", false);
            result.put("message", "이미 취소 처리된 예약입니다.");
            return result;
        }

        // 3. [검증 2] 날짜가 이미 지나 수강완료 처리된 클래스인지 확인
        if ("수강완료".equals(classStatus)) {
            result.put("success", false);
            result.put("message", "이미 수강이 완료되었거나 일정이 지난 클래스는 취소할 수 없습니다.");
            return result;
        }

        // 4. RESERVATION 테이블 STATUS '취소' 변경
        int resUpdate = rvd.updateReservationStatusCancel(reservationCode, memberCode);
        if (resUpdate <= 0) {
            result.put("success", false);
            result.put("message", "예약 취소 처리에 실패했습니다.");
            return result;
        }

        // 5. PAYMENT 테이블 STATUS '환불완료', REFUND = AMOUNT, REFUND_DATE = SYSDATE 변경
        int payUpdate = rvd.updatePaymentStatusRefund(reservationCode);

        if (payUpdate > 0) {
            result.put("success", true);
            result.put("message", "예약 및 결제 취소가 성공적으로 완료되었습니다.");
        } else {
            result.put("success", false);
            result.put("message", "결제 정보 환불 처리 중 오류가 발생했습니다.");
        }

        return result;
    }
}