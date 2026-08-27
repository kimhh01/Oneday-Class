package kr.co.oneclass.author.classmanage;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassManagementDTO {

    private int classCode;              // 클래스 식별 코드
    private long authorCode;             // 클래스 소유 작가 코드
    private String classTitle;          // 클래스명
    private String thumbnailPath;       // 목록에 출력할 대표 이미지 경로
    private String categoryName;        // 클래스 카테고리명
    private int regularPrice;           // 수강생이 실제 결제하는 클래스 정가
    private Date registeredDate;        // 클래스 등록일
    private Date recruitStartDate;      // 모집 시작일
    private Date recruitEndDate;        // 모집 종료일
    private int upcomingScheduleCount;  // 앞으로 진행될 예정 일정 수
    private int applicantCount;         // 현재 전체 신청 인원
    private String classStatus;         // CLASS.STATUS - 모집중, 준비중, 폐강
}
