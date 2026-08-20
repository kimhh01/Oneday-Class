package kr.co.oneclass.main;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@ToString
public class ScheduleDTO {
    private int scheduleCode;
    private int repeatRuleCode;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date classDate;
    private String startTime;
    private String endTime;
    private int minPeople;
    private int maxPeople;
    private int remainingPeople;
    private String soldOutYn;
}