package kr.co.oneclass.author.inquiry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquirySearchDTO {

    private long authorCode;
    private int inquiryTypeCode;
    private String keyword;
    private String answerStatus;
}
