package kr.co.oneclass.author.settlement;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SettlementAccountDTO {

    private long authorCode;        // 작가 코드
    private String businessName;   // 상호명
    private String authorName;     // 예금주 작가명
    private String bankName;       // 은행명
    private String accountNumber;  // 계좌번호
    private String bankbookPath;   // 통장사본 이미지 경로
    private Date updatedAt;        // 계좌 정보 수정일
}
