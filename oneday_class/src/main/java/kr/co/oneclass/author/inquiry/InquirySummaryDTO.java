package kr.co.oneclass.author.inquiry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquirySummaryDTO {

    private int totalCount;
    private int waitingCount;
    private int answeredCount;
}
