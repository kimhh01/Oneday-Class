package kr.co.oneclass.admin.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminWriterSummaryDomain {

	private long writerCode;
	private String writerName;
	private String workshopName;
	private String region;
	private String phone;
	private int classCount;
}
