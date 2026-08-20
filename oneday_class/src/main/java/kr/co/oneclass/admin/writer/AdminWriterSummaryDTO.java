package kr.co.oneclass.admin.writer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminWriterSummaryDTO {

	private long writerCode;
	private String writerName;
	private String workshopName;
	private String region;
	private String phone;
	private int classCount;
}
