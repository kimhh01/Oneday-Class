package kr.co.oneclass.author.classbasic.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassRegisterResultDTO {

    private int classCode;           // 클래스 코드
    private String classTitle;       // 클래스명
    private String thumbnailPath;    // 대표 이미지 경로
    private String classStatus;      // 등록 직후 클래스 상태
    private Date registeredDate;     // 클래스 등록일
    private String scheduleSummary;  // 일정 요약 문구
    private int desiredPrice;        // 희망가
}
