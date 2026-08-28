package kr.co.oneclass.author.classbasic;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassPreviewDTO {

    private ClassBasicDTO classBasic;                                  // 기본정보 단계 값
    private ClassLocationDTO classLocation;                            // 위치 단계 값
    private ClassScheduleDTO classSchedule;                            // 일정·가격 단계 값
    private ClassDetailDTO classDetail;                                // 상세정보 단계 값

    private List<ClassImageDTO> mainImageList = new ArrayList<>();     // 대표 이미지 목록
    private List<ClassImageDTO> resultImageList = new ArrayList<>();   // 완성작 이미지 목록

    private CurriculumFormDTO curriculum;                              // 커리큘럼 단계 값
}
