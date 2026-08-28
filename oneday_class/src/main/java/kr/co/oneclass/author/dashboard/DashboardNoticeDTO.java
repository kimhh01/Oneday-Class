package kr.co.oneclass.author.dashboard;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardNoticeDTO {

    private int noticeCode;
    private String noticeType;
    private String title;
    private Date inputDate;
}
