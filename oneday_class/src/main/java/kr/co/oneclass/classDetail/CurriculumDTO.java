package kr.co.oneclass.classDetail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor  // 👈 MyBatis 필수! (기본 생성자)
@AllArgsConstructor
public class CurriculumDTO {
    private long curriculumCode;
    private long classCode;
    private String curriculumType;
    private String curriculumTitle;
    private String curriculumContent;
    private String curriculumImg;
}
