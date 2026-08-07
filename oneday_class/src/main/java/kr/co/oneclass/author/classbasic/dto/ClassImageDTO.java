package kr.co.oneclass.author.classbasic.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassImageDTO {

    private int imageCode;      // 클래스 이미지 코드
    private int classCode;      // 이미지가 속한 클래스 코드
    private String imagePath;   // 저장된 이미지 경로
    private String imageType;   // 대표 / 완성작 / 갤러리 구분
    private String imageTitle;  // 이미지 제목
    private int imageOrder;     // 이미지 출력 순서
}
