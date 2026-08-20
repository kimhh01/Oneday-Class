package kr.co.oneclass.admin.writer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminWriterStatisticsDomain {

	private long writerCode;
	private int classCount;
	private int reservationCount;
	private int settlementAmount;
}
