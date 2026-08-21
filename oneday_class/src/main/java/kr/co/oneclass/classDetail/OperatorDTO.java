package kr.co.oneclass.classDetail;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor 
@AllArgsConstructor
public class OperatorDTO {
	private long operatorCode; 
	private int memberCode;
	private String approvalStatus;
	private String activityName;
	private String profileImage;
	private String creatorIntroduction;
	private String settlementAccount;
	private String settlementAccountImg;
	private String activityRegion;
	private String snsUrl;
	private Date joinDate;
}