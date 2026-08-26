package kr.co.oneclass.author.classapproval;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassApprovalDTO {

    private int classCode;           // 클래스 코드
    private long authorCode;          // 클래스를 등록한 작가 코드
    private String classTitle;       // 클래스명
    private String thumbnailPath;    // 목록에 출력할 대표 이미지 경로
    private String categoryName;     // 클래스 카테고리명
    private String scheduleType;     // 요일 반복 또는 개별 일정
    private int desiredPrice;        // 희망가
    private Date registeredDate;     // 클래스 등록일
    private Date recruitStartDate;   // 모집 시작일
    private Date recruitEndDate;     // 모집 종료일
    private String approvalStatus;   // 승인 상태: 작성중, 수정중, 대기, 승인, 반려, 중지
    private String classStatus;      // 운영 상태: 준비중, 대기중, 모집중, 폐강
    private String rejectionReason;  // 반려 사유
}
