package kr.co.oneclass.author.inquiry.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryListDTO {

    private int inquiryCode;
    private int inquiryTypeCode;
    private String inquiryTypeName;
    private String title;
    private Date inquiryDate;
    private boolean answered;
}
