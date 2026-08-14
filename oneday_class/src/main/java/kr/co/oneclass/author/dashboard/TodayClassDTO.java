package kr.co.oneclass.author.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TodayClassDTO {

    private int scheduleCode;       // 일정 코드
    private int classCode;          // 클래스 코드
    private String classTitle;      // 클래스명
    private String thumbnailPath;   // 클래스 대표 이미지 경로
    private String startTime;       // 수업 시작 시간
    private String endTime;         // 수업 종료 시간
    private int reservedCount;      // 예약 확정 인원
    private int capacity;           // 최대 모집 인원
    private String scheduleStatus;  // 일정 상태
    private String authorName;      // 작가명
}
