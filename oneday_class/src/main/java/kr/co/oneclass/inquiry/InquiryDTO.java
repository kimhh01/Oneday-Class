package kr.co.oneclass.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDTO {
    private int inquiryCode;
    private int inquiryTypeCode;
    private String inquiryTypeName;
    private int memberCode;
    private String title;
    private String content;
    private String inquiryImg;
    private String inquiryDate;
    private String status;        // 답변 상태 ('WAITING' / 'COMPLETED')
    private String answerContent; // 답변 내용
    private String answerDate;    // 답변 작성일
}