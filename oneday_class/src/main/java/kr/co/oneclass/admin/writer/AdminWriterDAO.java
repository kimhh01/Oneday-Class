package kr.co.oneclass.admin.writer;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminWriterDAO {

	int selectWriterCount(@Param("search") AdminWriterSearchDTO searchDTO);

	List<AdminWriterSummaryDTO> selectWriterList(@Param("search") AdminWriterSearchDTO searchDTO);

	AdminWriterDetailDTO selectWriterDetail(@Param("writerCode") long writerCode);

	AdminWriterStatisticsDTO selectWriterStatistics(@Param("writerCode") long writerCode);

	List<AdminWriterClassDTO> selectWriterClassList(@Param("writerCode") long writerCode);
}
