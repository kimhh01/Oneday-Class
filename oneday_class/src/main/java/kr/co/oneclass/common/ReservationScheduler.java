package kr.co.oneclass.common;

import kr.co.oneclass.purchase.PurchaseDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationScheduler {

    @Autowired
    private PurchaseDAO purchaseDAO;

    // 💡 1. 서버가 완전히 구동된 직후 즉시 1회 실행
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        System.out.println("[Scheduler] 서버 구동 완료 - 초기 예약 상태 업데이트를 실행합니다.");
        autoUpdateExpiredReservations();
    }

    // 💡 2. 이후 10분마다 주기적으로 자동 실행
    @Scheduled(cron = "0 */10 * * * *")
    public void autoUpdateExpiredReservations() {
        try {
            int count = purchaseDAO.updateExpiredReservationStatus();
            if (count > 0) {
                System.out.println("[Scheduler] 지나간 예약 " + count + "건을 '수강완료' 상태로 변경했습니다.");
            }
        } catch (Exception e) {
            System.err.println("[Scheduler Error] 예약 상태 변경 중 오류 발생: " + e.getMessage());
        }
    }
}