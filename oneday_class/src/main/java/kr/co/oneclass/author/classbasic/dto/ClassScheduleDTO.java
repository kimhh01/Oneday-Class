package kr.co.oneclass.author.classbasic.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassScheduleDTO {

    private int classCode;                                                   // 클래스 코드
    private long authorCode;                                                 // 작가 코드(소유권 검증)
    private String scheduleType;                                             // 요일반복 / 개별일정
    private Date recruitStartDate;                                           // 모집 시작일
    private Date recruitEndDate;                                             // 모집 종료일
    private Integer regularPrice;                                            // 정가
    private Integer desiredPrice;                                            // 희망가
    private Integer minPeople;                                               // 최소인원
    private Integer maxPeople;                                               // 최대인원
    private int refundPolicyCode;                                            // 환불 기준
    private boolean materialIncluded;                                        // 재료비 포함 여부

    private List<RepeatScheduleDTO> repeatScheduleList = new ArrayList<>();  // 요일 반복 일정 입력값
    private List<ScheduleDTO> scheduleList = new ArrayList<>();              // 개별 일정 입력값
}
