package kr.co.oneclass.purchase;

import java.util.List;
import java.util.Map;

public interface PurchaseService {

    /**
     * 1. 회원별 구매/예약 내역 목록 조회
     */
    List<Purchase> getPurchaseList(String memberCode, String status);

    /**
     * 2. 구매/예약 상세 내역 조회
     */
    Purchase getPurchaseDetail(String reservationCode);

    // 💡 [추가] 예약 취소 메서드
    Map<String, Object> cancelPurchase(String reservationCode, int memberCode);
    
}