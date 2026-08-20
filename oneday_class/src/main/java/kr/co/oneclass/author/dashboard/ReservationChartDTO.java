package kr.co.oneclass.author.dashboard;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservationChartDTO {

    private Date reservationDate;  // 예약 발생일
    private int reservationCount;  // 해당 일자의 예약 수
}
