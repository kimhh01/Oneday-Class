package kr.co.oneclass.admin.writer;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminWriterDetailDomain {

	private long writerCode;
	private String writerName;
	private String workshopName;

	private String email;
	private String mobilePhone;

	private String profileImage;
	private String activityRegion;
	private String snsUrl;

	private Date joinDate;

	private String settlementAccount;
	private String settlementAccountImg;

	private String introduction;
}
