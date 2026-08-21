package kr.co.oneclass.author.classmanage;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleOperationDTO {

    private int classCode;
    private int scheduleCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate scheduleDate;
    private String startTime;
    private String endTime;
    private int minPeople;
    private int maxPeople;
}
