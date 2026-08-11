package kr.co.oneclass.purchase;

import java.util.List;

public interface PurchaseService {

    /**
     * 1. 회원별 구매/예약 내역 목록 조회
     */
    List<Purchase> getPurchaseList(String memberCode, String status);

    /**
     * 2. 구매/예약 상세 내역 조회
     */
    Purchase getPurchaseDetail(String reservationCode);

    /**
     * 3. 리뷰 작성
     */
    boolean writeReview(Object rdto);

    /**
     * 4. 작성한 리뷰 조회
     */
    Object reviewView(int memberCode, int classCode);
}