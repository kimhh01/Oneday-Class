package kr.co.oneclass.admin.writer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminWriterStatisticsDTO {

	private long writerCode;
	private int classCount;
	private int reservationCount;
	private int settlementAmount;
}
