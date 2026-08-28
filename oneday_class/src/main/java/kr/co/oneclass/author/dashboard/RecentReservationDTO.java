package kr.co.oneclass.author.dashboard;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecentReservationDTO {

    private int reservationCode;
    private int classCode;
    private String classTitle;
    private String categoryName;
    private String memberName;
    private int peopleNumber;
    private Date reservationDate;
    private String reservationStatus;
}
