package kr.co.oneclass.author.classbasic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassBasicDTO {

    private int classCode;             // 클래스 코드
    private long authorCode;            // 클래스를 등록한 작가 코드
    private int categoryCode;          // 클래스 카테고리 코드
    private String categoryName;       // 카테고리명
    private String classTitle;         // 클래스명
    private String shortIntroduction;  // 한 줄 소개
    private String classIntroduction;  // 클래스 소개
    private String mainImagePath;      // 현재 대표 이미지
    private String registerStep;       // 마지막으로 열어 둔 등록 단계
}
