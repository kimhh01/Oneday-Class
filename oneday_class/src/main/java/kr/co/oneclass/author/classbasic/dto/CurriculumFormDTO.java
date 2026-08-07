package kr.co.oneclass.author.classbasic.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CurriculumFormDTO {

    private int classCode;                                         // 클래스 코드
    private long authorCode;                                       // 작가 코드(소유권 검증)
    private List<CurriculumStepDTO> stepList = new ArrayList<>();  // 커리큘럼 단계 목록
}
