package kr.co.oneclass.admin.notice;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminNoticeDAO {
	int selectNoticeTotalCount(@Param("search") AdminNoticeSearchDTO dto);

	List<AdminNoticeSummaryDTO> selectNoticeList(@Param("search") AdminNoticeSearchDTO dto);

	AdminNoticeDetailDTO selectNoticeDetail(@Param("noticeCode") int noticeCode);

	int insertNotice(AdminNoticeCreateDTO dto);

	int updateNotice(AdminNoticeUpdateDTO dto);
}
