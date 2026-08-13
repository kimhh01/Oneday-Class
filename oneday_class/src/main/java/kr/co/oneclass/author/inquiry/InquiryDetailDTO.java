package kr.co.oneclass.author.inquiry;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryDetailDTO {

    private int inquiryCode;
    private int inquiryTypeCode;
    private String inquiryTypeName;
    private String title;
    private String content;
    private String answer;
    private Date inquiryDate;
    private Date answerDate;
    private String inquiryImg;
    private String managerName;
    private boolean answered;
}
