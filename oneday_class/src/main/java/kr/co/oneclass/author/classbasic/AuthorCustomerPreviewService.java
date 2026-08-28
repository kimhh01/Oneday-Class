package kr.co.oneclass.author.classbasic;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.oneclass.classDetail.AdditionalInfoDTO;
import kr.co.oneclass.classDetail.ClassDetailDAO;
import kr.co.oneclass.classDetail.ClassDetailResponseDTO;
import kr.co.oneclass.classDetail.CurriculumDTO;
import kr.co.oneclass.classDetail.DetailInfoDTO;
import kr.co.oneclass.classDetail.MaterialDTO;
import kr.co.oneclass.classDetail.OfferingDTO;
import kr.co.oneclass.classDetail.OperatorDTO;
import kr.co.oneclass.classDetail.ReviewDTO;
import kr.co.oneclass.classDetail.ReviewImgDTO;
import kr.co.oneclass.classDetail.ReviewSummaryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ClassImageDTO;
import kr.co.oneclass.common.ScheduleDTO;
import kr.co.oneclass.common.TagDTO;

@Service
public class AuthorCustomerPreviewService {

    private final ClassDetailDAO classDetailDAO;

    public AuthorCustomerPreviewService(ClassDetailDAO classDetailDAO) {
        this.classDetailDAO = classDetailDAO;
    }

    // 작성 중 초안도 실제 수강생용 상세 화면 구조로 조립한다.
    public ClassDetailResponseDTO getCustomerPreview(int classCode) {
        ClassDTO classInfo = classDetailDAO.selectClass(classCode);
        if (classInfo == null) {
            return null;
        }

        List<ClassImageDTO> images = classDetailDAO.selectClassImageList(classCode);
        classInfo.setImageList(images);

        List<ReviewDTO> reviews = classDetailDAO.selectReviewList(classCode);
        if (reviews != null) {
            for (ReviewDTO review : reviews) {
                List<ReviewImgDTO> reviewImages =
                        classDetailDAO.selectReviewImgList(review.getReviewCode());
                review.setReviewImg(reviewImages);
            }
        }

        long operatorCode = classInfo.getOperatorCode();
        int categoryCode = classInfo.getCategoryCode();
        OperatorDTO creator = classDetailDAO.selectCreator(operatorCode);
        List<ClassDTO> sameCategory =
                classDetailDAO.selectSameCategoryList(classCode, categoryCode);
        List<CurriculumDTO> curriculum = classDetailDAO.selectCurriculum(classCode);
        ReviewSummaryDTO reviewSummary = classDetailDAO.selectReviewSummary(classCode);
        List<ScheduleDTO> representativeSchedule = classDetailDAO.selectSchedule(classCode);
        List<ScheduleDTO> schedules = classDetailDAO.selectScheduleList(classCode);
        List<MaterialDTO> materials = classDetailDAO.selectMaterialList(classCode);
        List<OfferingDTO> offerings = classDetailDAO.selectOfferingList(classCode);
        List<TagDTO> tags = classDetailDAO.selectTagList(classCode);
        List<DetailInfoDTO> detailInfo = classDetailDAO.selectDetailInfoList(classCode);
        List<AdditionalInfoDTO> additionalInfo = classDetailDAO.selectAdditionalInfo(classCode);

        return new ClassDetailResponseDTO(classInfo, creator, sameCategory, curriculum,
                reviews, reviewSummary, representativeSchedule, schedules, materials,
                offerings, tags, detailInfo, additionalInfo);
    }
}
