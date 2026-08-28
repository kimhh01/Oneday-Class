package kr.co.oneclass.author.classmanage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleManagementSummaryDTO {

    private int scheduleCount;
    private int recruitingScheduleCount;
    private int reservedCount;
    private int remainingSeatCount;
}
