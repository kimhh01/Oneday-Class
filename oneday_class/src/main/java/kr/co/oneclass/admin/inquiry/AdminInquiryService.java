package kr.co.oneclass.admin.inquiry;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.admin.common.PageDomain;

@Service
public class AdminInquiryService {
	private final AdminInquiryDAO inquiryDAO;

	public AdminInquiryService(AdminInquiryDAO inquiryDAO) {
		this.inquiryDAO = inquiryDAO;
	}

	public AdminInquiryStatisticsDomain getInquiryStatistics() {
		AdminInquiryStatisticsDTO dto = inquiryDAO.selectInquiryStatistics();
		return dto == null ? new AdminInquiryStatisticsDomain(0, 0, 0)
				: new AdminInquiryStatisticsDomain(dto.getTotalCount(), dto.getWaitingCount(), dto.getCompletedCount());
	}

	public List<AdminInquirySummaryDomain> getInquiryList(AdminInquirySearchDTO dto) {
		setPage(dto);
		return inquiryDAO.selectInquiryList(dto).stream()
				.map(d -> new AdminInquirySummaryDomain(d.getInquiryCode(), d.getInquiryTypeName(), d.getTitle(),
						d.getWriterName(), d.getWriterType(), d.getInquiryDate(), d.getAnswerStatus()))
				.toList();
	}

	public PageDomain getPage(AdminInquirySearchDTO dto) {

		int currentPage = Math.max(dto.getPage(), 1);
		int pageSize = Math.max(dto.getPageSize(), 1);

		int totalCount = inquiryDAO.selectInquiryCount(dto);

		int totalPage = (int) Math.ceil((double) totalCount / pageSize);

		int startPage = ((currentPage - 1) / 5) * 5 + 1;

		int endPage = Math.min(startPage + 4, totalPage);

		return new PageDomain(totalCount, currentPage, pageSize, totalPage, startPage, endPage);
	}

	public AdminInquiryDetailDomain getInquiryDetail(int inquiryCode) {
		AdminInquiryDetailDTO d = inquiryDAO.selectInquiryDetail(inquiryCode);
		return d == null ? null
				: new AdminInquiryDetailDomain(d.getInquiryCode(), d.getInquiryTypeCode(), d.getInquiryTypeName(),
						d.getManagerCode(), d.getOperatorCode(), d.getMemberCode(), d.getWriterName(),
						d.getWriterType(), d.getTitle(), d.getContent(), d.getInquiryImg(), d.getInquiryDate(),
						d.getAnswer(), d.getAnswerDate(), d.getAnswerStatus());
	}

	public List<AdminInquiryTypeDomain> getInquiryTypeList() {
		return inquiryDAO.selectInquiryTypeList().stream()
				.map(d -> new AdminInquiryTypeDomain(d.getInquiryTypeCode(), d.getInquiryTypeName())).toList();
	}

	@Transactional
	public boolean registerInquiryAnswer(AdminInquiryAnswerDTO dto) {
		return inquiryDAO.updateInquiryAnswer(dto) > 0;
	}

	private void setPage(AdminInquirySearchDTO dto) {
		int page = Math.max(dto.getPage(), 1);
		int size = Math.max(dto.getPageSize(), 1);
		dto.setStartRow((page - 1) * size + 1);
		dto.setEndRow(page * size);
	}
}
