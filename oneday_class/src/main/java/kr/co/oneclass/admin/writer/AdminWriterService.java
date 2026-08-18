package kr.co.oneclass.admin.writer;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class AdminWriterService {

	private AdminWriterDAO writerDAO;

	public AdminWriterService(AdminWriterDAO writerDAO) {

		this.writerDAO = writerDAO;
	}

	public List<AdminWriterSummaryDomain> getWriterList(AdminWriterSearchDTO searchDTO) {

		setPage(searchDTO);

		return writerDAO.selectWriterList(searchDTO).stream().map(this::toSummaryDomain).toList();
	}

	public int getWriterCount(AdminWriterSearchDTO searchDTO) {

		return writerDAO.selectWriterCount(searchDTO);
	}

	public AdminWriterDetailDomain getWriterDetail(long writerCode) {

		AdminWriterDetailDTO dto = writerDAO.selectWriterDetail(writerCode);

		return dto == null ? null : toDetailDomain(dto);
	}

	public AdminWriterStatisticsDomain getWriterStatistics(long writerCode) {

		AdminWriterStatisticsDTO dto = writerDAO.selectWriterStatistics(writerCode);

		return dto == null ? new AdminWriterStatisticsDomain(writerCode, 0, 0, 0)
				: new AdminWriterStatisticsDomain(dto.getWriterCode(), dto.getClassCount(), dto.getReservationCount(),
						dto.getSettlementAmount());
	}

	public List<AdminWriterClassDomain> getWriterClassList(long writerCode) {

		return writerDAO.selectWriterClassList(writerCode).stream().map(this::toWriterClassDomain).toList();
	}

	private void setPage(AdminWriterSearchDTO dto) {

		int page = Math.max(dto.getPage(), 1);
		int pageSize = Math.max(dto.getPageSize(), 1);

		dto.setStartRow((page - 1) * pageSize + 1);

		dto.setEndRow(page * pageSize);
	}

	private AdminWriterSummaryDomain toSummaryDomain(AdminWriterSummaryDTO dto) {

		return new AdminWriterSummaryDomain(dto.getWriterCode(), dto.getWriterName(), dto.getWorkshopName(),
				dto.getRegion(), dto.getPhone(), dto.getClassCount());
	}

	private AdminWriterDetailDomain toDetailDomain(AdminWriterDetailDTO dto) {

		return new AdminWriterDetailDomain(dto.getWriterCode(), dto.getWriterName(), dto.getWorkshopName(),
				dto.getEmail(), dto.getMobilePhone(), dto.getProfileImage(), dto.getActivityRegion(), dto.getSnsUrl(),
				dto.getJoinDate(), dto.getSettlementAccount(), dto.getSettlementAccountImg(), dto.getIntroduction());
	}

	private AdminWriterClassDomain toWriterClassDomain(AdminWriterClassDTO dto) {

		return new AdminWriterClassDomain(dto.getClassCode(), dto.getClassName(), dto.getPeriodType(),
				dto.getRecruitmentStartDate(), dto.getRecruitmentEndDate(), dto.getMinimumPeople(),
				dto.getMaximumPeople(), dto.getPrice(), dto.getClassStatus());
	}
}
