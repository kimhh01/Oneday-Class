package kr.co.oneclass.author.classbasic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CurriculumStepDTO {

    private int curriculumCode;            // 커리큘럼 코드
    private int classCode;                 // 커리큘럼이 속한 클래스 코드
    private int stepNo;                    // 커리큘럼 단계 순번
    private String title;                  // 단계 제목
    private String content;                // 단계 내용
    private String imagePath;              // CLASS_CURRICULUM.CURRICULUM_IMG
    private MultipartFile imageFile;       // 화면에서 새로 선택한 단계 이미지
}
