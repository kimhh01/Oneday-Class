package kr.co.oneclass.author.inquiry.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryFormDTO {

    private int inquiryCode;
    private long authorCode;
    private int inquiryTypeCode;
    private String title;
    private String content;
    private String inquiryImg;
}
