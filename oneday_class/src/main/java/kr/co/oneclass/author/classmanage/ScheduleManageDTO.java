package kr.co.oneclass.author.classmanage;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleManageDTO {

    private int scheduleCode;       // 일정 코드
    private int classCode;          // 일정이 속한 클래스 코드
    private String classTitle;      // 일정이 속한 클래스명
    private String classStatus;     // 클래스 운영 상태: 모집중, 준비중, 폐강
    private Date scheduleDate;      // 수업 진행일
    private String startTime;       // 수업 시작 시간
    private String endTime;         // 수업 종료 시간
    private int minPeople;          // 클래스 개설 최소 인원
    private int maxPeople;          // 일정의 최대 모집 인원
    private int reservedCount;      // 현재 신청이 확정된 인원
    private int remainingPeople;    // 추가로 모집 가능한 남은 인원
    private String scheduleStatus;  // 시작·종료 일시 파생값: 모집중, 모집 마감, 진행 중, 진행 완료
    private String editableYn;      // 수업 시작 전이면 Y, 시작했거나 종료됐으면 N
}
