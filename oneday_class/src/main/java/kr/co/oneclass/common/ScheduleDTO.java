package kr.co.oneclass.common;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleDTO {
    private int scheduleCode;      // 스케줄 코드
    private int classCode;         // 클래스 코드
    private int repeatRuleCode;    // 반복 규칙 코드
    private Date classDate;        // 수업일 (년-월-일)
    
    private String startTime;  
    private String endTime;         // 수업 종료 시간 (시:분:초)
    
    private int minPeople;         // 최소 인원
    private int maxPeople;         // 최대 인원
    private int remainingPeople;   // 잔여 인원
    private String soldOutYn;      // 매진 여부(Y/N)
    
    private Date startDate;        // 운영 시작일 (OPERATION_START_DATE)
    private Date endDate;          // 운영 종료일 (OPERATION_END_DATE)
    
    public long getDiffHours() {
        if (startTime == null || endTime == null) return 0;
        
        java.time.LocalTime start = java.time.LocalTime.parse(this.startTime);
        java.time.LocalTime end = java.time.LocalTime.parse(this.endTime);
        
        return java.time.Duration.between(start, end).toHours();
    }
}