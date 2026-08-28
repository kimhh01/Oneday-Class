package kr.co.oneclass.author.classbasic;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassDetailDTO {

    private int classCode;                                             // 클래스 코드
    private long authorCode;                                           // 작가 코드(소유권 검증)
    private List<String> advantageList = new ArrayList<>();            // 이런점이 좋아요
    private List<String> recommendList = new ArrayList<>();            // 이런분께 추천해요
    private String resultName;                                         // 클래스 완성작 이름
    private String resultDescription;                                  // 클래스 완성작 설명
    private List<Integer> optionCodeList = new ArrayList<>();          // 주차, 와이파이 등 제공 항목
    private List<String> noticeList = new ArrayList<>();               // 주의사항
    private List<String> tagList = new ArrayList<>();                  // 태그 입력
    private List<String> materialNameList = new ArrayList<>();         // 준비물·재료명
    private List<String> materialContentList = new ArrayList<>();      // 준비물·재료 설명

    private List<ClassImageDTO> resultImageList = new ArrayList<>();   // 클래스 완성작 사진
    private List<Integer> removeImageCodeList = new ArrayList<>();     // 삭제할 기존 완성작 이미지
}
