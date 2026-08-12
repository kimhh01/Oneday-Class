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
@NoArgsConstructor  // 👈 MyBatis 데이터 매핑용 기본 생성자 추가
@AllArgsConstructor
public class OperatorDTO {
	private long operatorCode; // 👈 int -> long 변경 (오버플로우/캐스팅 에러 방지)
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