package kr.co.oneclass.purchase;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseDAO {

    /**
     * 1. 회원별 구매/예약 내역 목록 조회
     */
    List<Purchase> selectListByMember(@Param("memberCode") String memberCode, 
                                     @Param("status") String status);

    /**
     * 2. 구매/예약 상세 내역 조회
     */
    Purchase selectDetail(@Param("reservationCode") String reservationCode);

    /**
     * 3. 예약 테이블 상태 변경 ('취소')
     */
    int updateReservationStatusCancel(@Param("reservationCode") String reservationCode, 
                                     @Param("memberCode") int memberCode);

    /**
     * 4. 결제 테이블 상태 변경 ('환불완료', REFUND=AMOUNT, REFUND_DATE=SYSDATE)
     */
    int updatePaymentStatusRefund(@Param("reservationCode") String reservationCode);
}