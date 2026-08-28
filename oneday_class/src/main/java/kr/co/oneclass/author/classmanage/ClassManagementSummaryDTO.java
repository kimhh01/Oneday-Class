package kr.co.oneclass.author.classmanage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassManagementSummaryDTO {

    private int openClassCount;        // 현재 모집 중인 승인 클래스 수
    private int hiddenClassCount;      // 현재 비공개인 승인 클래스 수
    private int closedClassCount;      // 폐강된 승인 클래스 수
    private int monthlyScheduleCount;  // 이번 달에 진행할 일정 수
    private int totalApplicantCount;   // 승인 클래스의 누적 예약 인원
    private long monthlySalesAmount;   // 이번 달 결제 완료·미환불 매출
}
