package kr.co.oneclass.admin.onedayclass;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.admin.common.PageDomain;
import kr.co.oneclass.common.AESUtil;

@Service
public class AdminClassService {

	private final AdminClassDAO classDAO;

	public AdminClassService(AdminClassDAO classDAO) {

		this.classDAO = classDAO;
	}

	public List<AdminClassSummaryDomain> getClassList(AdminClassSearchDTO searchDTO) {

		setPage(searchDTO);

		return classDAO.selectClassList(searchDTO).stream().map(this::toClassSummaryDomain).toList();
	}

	public int getClassCount(AdminClassSearchDTO searchDTO) {

		return classDAO.selectClassCount(searchDTO);
	}

	public AdminClassDetailDomain getClassDetail(int classCode) {

		AdminClassDetailDTO dto = classDAO.selectClassDetail(classCode);

		return dto == null ? null : toClassDetailDomain(dto);
	}

	public List<AdminClassImageDomain> getClassImageList(int classCode) {

		return classDAO.selectClassImageList(classCode).stream()
				.map(dto -> new AdminClassImageDomain(dto.getImageCode(), dto.getClassCode(), dto.getImageType(),
						dto.getImagePath(), dto.getImageOrder()))
				.toList();
	}

	public List<AdminClassTagDomain> getClassTagList(int classCode) {

		return classDAO.selectClassTagList(classCode).stream()
				.map(dto -> new AdminClassTagDomain(dto.getTagCode(), dto.getClassCode(), dto.getTagName())).toList();
	}

	public List<AdminClassScheduleDomain> getClassScheduleList(int classCode) {

		return classDAO.selectClassScheduleList(classCode).stream()
				.map(dto -> new AdminClassScheduleDomain(dto.getScheduleCode(), dto.getClassCode(), dto.getClassDate(),
						dto.getStartTime(), dto.getEndTime(), dto.getMinimumPeople(), dto.getMaximumPeople()))
				.toList();
	}

	public List<AdminClassCurriculumDomain> getClassCurriculumList(int classCode) {

		return classDAO.selectClassCurriculumList(classCode).stream()
				.map(dto -> new AdminClassCurriculumDomain(dto.getCurriculumCode(), dto.getClassCode(),
						dto.getStepNumber(), dto.getCurriculumTitle(), dto.getCurriculumDescription(),
						dto.getCurriculumImage()))
				.toList();
	}

	public List<AdminClassMaterialDomain> getClassMaterialList(int classCode) {

		return classDAO.selectClassMaterialList(classCode).stream()
				.map(dto -> new AdminClassMaterialDomain(dto.getMaterialCode(), dto.getClassCode(),
						dto.getMaterialName(), dto.getMaterialContent()))
				.toList();
	}

	public List<AdminClassOfferingDomain> getClassOfferingList(int classCode) {

		return classDAO.selectClassOfferingList(classCode).stream().map(
				dto -> new AdminClassOfferingDomain(dto.getOfferingCode(), dto.getClassCode(), dto.getOfferingName()))
				.toList();
	}

	public List<AdminClassAdditionalInfoDomain> getClassAdditionalInfoList(int classCode) {

		return classDAO.selectClassAdditionalInfoList(classCode).stream()
				.map(dto -> new AdminClassAdditionalInfoDomain(dto.getAdditionalInfoCode(), dto.getClassCode(),
						dto.getContent()))
				.toList();
	}

	public List<AdminFinishedProductDomain> getFinishedProductList(int classCode) {

		return classDAO.selectFinishedProductList(classCode).stream()
				.map(dto -> new AdminFinishedProductDomain(dto.getFinishedProductCode(), dto.getClassCode(),
						dto.getFinishedProductImage()))
				.toList();
	}

	@Transactional
	public boolean updateClassStatus(int classCode) {

		return classDAO.updateClassStatus(classCode) > 0;
	}

	@Transactional
	public boolean approveClass(int classCode) {

		return classDAO.updateClassApproval(classCode) > 0;
	}

	@Transactional
	public boolean rejectClass(int classCode, AdminClassReviewDTO dto) {

		return classDAO.updateClassRejection(classCode, dto) > 0;
	}

	private void setPage(AdminClassSearchDTO dto) {

		int page = Math.max(dto.getPage(), 1);
		int pageSize = Math.max(dto.getPageSize(), 1);

		dto.setStartRow((page - 1) * pageSize + 1);

		dto.setEndRow(page * pageSize);
	}

	private AdminClassSummaryDomain toClassSummaryDomain(AdminClassSummaryDTO dto) {

		return new AdminClassSummaryDomain(dto.getClassCode(), dto.getClassName(), dto.getRegion(),
				dto.getRunningTime(), decrypt(dto.getWriterName()), dto.getWorkshopName(), dto.getCategoryName(),
				dto.getRecruitmentStartDate(), dto.getRecruitmentEndDate(), dto.getSalePrice(), dto.getApprovalStatus(),
				dto.getClassStatus());
	}

	private AdminClassDetailDomain toClassDetailDomain(AdminClassDetailDTO dto) {

		return new AdminClassDetailDomain(dto.getClassCode(), dto.getClassName(), dto.getWriterCode(),
				decrypt(dto.getWriterName()), dto.getWorkshopName(), dto.getCategoryName(), dto.getRegion(),
				dto.getRunningTime(), dto.getClassStatus(), dto.getSalePrice(), dto.getMarketPrice(),
				dto.getDiscountRate(), dto.getMinimumPeople(), dto.getMaximumPeople(), dto.getRecruitmentStartDate(),
				dto.getRecruitmentEndDate(), dto.getPeriodType(), dto.getSingleIntroduce(), dto.getIntroduce(),
				dto.getFinishedProductDescription(), dto.getApprovalStatus(), dto.getApprovalMemo());
	}

	public PageDomain getPage(AdminClassSearchDTO searchDTO) {

		int currentPage = Math.max(searchDTO.getPage(), 1);

		int totalCount = classDAO.selectClassCount(searchDTO);

		int pageSize = Math.max(searchDTO.getPageSize(), 1);

		int totalPage = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / pageSize);

		int startPage = ((currentPage - 1) / 5) * 5 + 1;

		int endPage = Math.min(startPage + 4, totalPage);

		return new PageDomain(totalCount, currentPage, pageSize, totalPage, startPage, endPage);
	}

	private String decrypt(String value) {

		if (value == null || value.isBlank()) {
			return value;
		}

		return AESUtil.decrypt(value);
	}
}
