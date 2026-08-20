package kr.co.oneclass.author.classbasic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClassSubmitDTO {

    private int classCode;                   // 클래스 코드
    private long authorCode;                 // 작가 코드(소유권 검증)
    private boolean serviceTermsAgreed;      // 서비스 이용약관 동의 여부
    private boolean operationPrivacyAgreed;  // 운영·개인정보 처리 동의 여부
}
