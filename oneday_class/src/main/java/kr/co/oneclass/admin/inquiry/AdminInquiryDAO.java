package kr.co.oneclass.admin.inquiry;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminInquiryDAO {
	AdminInquiryStatisticsDTO selectInquiryStatistics();

	int selectInquiryCount(@Param("search") AdminInquirySearchDTO searchDTO);

	List<AdminInquirySummaryDTO> selectInquiryList(@Param("search") AdminInquirySearchDTO searchDTO);

	AdminInquiryDetailDTO selectInquiryDetail(@Param("inquiryCode") int inquiryCode);

	List<AdminInquiryTypeDTO> selectInquiryTypeList();

	int updateInquiryAnswer(AdminInquiryAnswerDTO answerDTO);
}
