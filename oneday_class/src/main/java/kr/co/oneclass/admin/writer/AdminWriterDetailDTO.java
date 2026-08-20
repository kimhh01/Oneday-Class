package kr.co.oneclass.admin.writer;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminWriterDetailDTO {

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
