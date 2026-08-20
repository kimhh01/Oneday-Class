package kr.co.oneclass.author.classbasic;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {

    private int scheduleCode;       // 일정 코드
    private int classCode;          // 일정이 속한 클래스 코드
    private int repeatRuleCode;     // CLASS_REPEAT_RULE 코드
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date scheduleDate;      // 수업 진행일
    private String startTime;       // 수업 시작 시간
    private String endTime;         // 수업 종료 시간
    private int minPeople;          // 클래스 개설 최소 인원
    private int maxPeople;          // 일정의 최대 모집 인원
    private String scheduleStatus;  // 일정 상태
}
