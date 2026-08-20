package kr.co.oneclass.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private int reservationCode;
    private int classCode;
    private int memberCode;
    private int scheduleCode;
    private Date reservationDate;
    private int peopleNum;
    private String status;
    private int price;
    private String className;
    private String classImg;
    private String activityName;

}