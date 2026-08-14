package kr.co.oneclass.author.classbasic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RepeatScheduleDTO {

    private int repeatScheduleCode;                          // 반복 일정 규칙 코드
    private int classCode;                                   // 클래스 코드
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date repeatStartDate;                            // 일정 시작일
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date repeatEndDate;                              // 일정 종료일
    private List<String> dayOfWeekList = new ArrayList<>();  // 반복 요일 목록
    private String startTime;                                // 시작 시간
    private String endTime;                                  // 종료 시간
    private int minPeople;                                   // 최소인원
    private int maxPeople;                                   // 최대인원
}
