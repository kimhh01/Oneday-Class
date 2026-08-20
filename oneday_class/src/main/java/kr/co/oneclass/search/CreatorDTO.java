package kr.co.oneclass.search;

import kr.co.oneclass.main.ClassDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class CreatorDTO {
    private int operatorCode;
    private int memberCode;
    private String approvalStatus;
    private String activityName;        // 작가/활동명
    private String profileImage;
    private String creatorIntroduction;
    private String settlementAccount;
    private String settlementAccountImg;
    private String activityRegion;
    private String snsUrl;
    private Date joinDate;
    
    private List<ClassDTO> classList;   // 작가가 보유한 클래스 목록
}