package kr.co.oneclass.admin.onedayclass;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminClassDAO {

	int selectClassCount(@Param("search") AdminClassSearchDTO searchDTO);

	List<AdminClassSummaryDTO> selectClassList(@Param("search") AdminClassSearchDTO searchDTO);

	AdminClassDetailDTO selectClassDetail(@Param("classCode") int classCode);

	List<AdminClassImageDTO> selectClassImageList(@Param("classCode") int classCode);

	List<AdminClassTagDTO> selectClassTagList(@Param("classCode") int classCode);

	List<AdminClassScheduleDTO> selectClassScheduleList(@Param("classCode") int classCode);

	List<AdminClassCurriculumDTO> selectClassCurriculumList(@Param("classCode") int classCode);

	List<AdminClassMaterialDTO> selectClassMaterialList(@Param("classCode") int classCode);

	List<AdminClassOfferingDTO> selectClassOfferingList(@Param("classCode") int classCode);

	List<AdminClassAdditionalInfoDTO> selectClassAdditionalInfoList(@Param("classCode") int classCode);

	List<AdminFinishedProductDTO> selectFinishedProductList(@Param("classCode") int classCode);

	int updateClassStatus(@Param("classCode") int classCode);

	int updateClassApproval(@Param("classCode") int classCode);

	int updateClassRejection(@Param("classCode") int classCode, @Param("dto") AdminClassReviewDTO dto);
}
