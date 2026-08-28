package kr.co.oneclass.author.classmanage;

import java.util.ArrayList;
import java.util.List;

import kr.co.oneclass.author.classbasic.ClassPreviewDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassManagementDetailDTO {

    private ClassPreviewDTO classPreview;                              // 기존 클래스 등록 미리보기 전체 정보
    private List<ScheduleManageDTO> scheduleList = new ArrayList<>();  // 일정별 신청 인원과 모집 가능 인원
    private int totalApplicantCount;                                   // 클래스 전체 신청 인원
    private int upcomingScheduleCount;                                 // 앞으로 진행할 일정 수
    private String classStatus;                                        // 현재 클래스 공개·비공개·폐쇄 상태
    private long salesAmount;                                          // 결제 완료·미환불 누적 판매 금액

    public long getSettlementFeeAmount() {
        return Math.round(salesAmount * 0.1d);
    }

    public long getExpectedSettlementAmount() {
        return salesAmount - getSettlementFeeAmount();
    }
}
