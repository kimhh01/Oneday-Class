package kr.co.oneclass.author.classbasic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassLocationDTO {

    private int classCode;
    private long authorCode;
    private String zipcode;
    private String address;
    private String oldAddress;
    private String detailAddress;
    private String locationGuide; // DB 전용 컬럼이 없어 기존 관리화면 호환용으로만 유지
    private double latitude;
    private double longitude;
}
