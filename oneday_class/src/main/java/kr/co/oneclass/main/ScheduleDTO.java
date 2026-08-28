package kr.co.oneclass.main;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class ScheduleDTO {
    private int scheduleCode;
    private int repeatRuleCode;
    
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String startTime;
    private String endTime;
    private int minPeople;
    private int maxPeople;
    private int remainingPeople;
    private String soldOutYn;
}