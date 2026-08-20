package kr.co.oneclass.inquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inquiry {
    private int inquiryCode;
    private Date inquiryDate;
    private String inquiryType;
    private String status;
    private String title;
}