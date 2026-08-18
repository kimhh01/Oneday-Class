package kr.co.oneclass.classDetail;

import java.util.List;

import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
public class ClassDetailResponseDTO {
    private ClassDTO classDetail;
    private OperatorDTO creator;
    private List<ClassDTO> sameCategoryList;
    private List<CurriculumDTO> curriculumList;
    private List<ReviewDTO> reviewList;
    private ReviewSummaryDTO reviewSummary;
    private List<ScheduleDTO> representativeSchedule;
    private List<ScheduleDTO> scheduleList;
    private List<MaterialDTO> materialList;
    private List<OfferingDTO> offeringList;

    // 생성자, Getter, Setter 작성 (또는 Lombok @Data 사용)
    public ClassDetailResponseDTO(ClassDTO classDetail, OperatorDTO creator, 
                                  List<ClassDTO> sameCategoryList, List<CurriculumDTO> curriculumList, 
                                  List<ReviewDTO> reviewList, ReviewSummaryDTO reviewSummary, 
                                  List<ScheduleDTO> representativeSchedule, List<ScheduleDTO> scheduleList,
                                  List<MaterialDTO> materialList, List<OfferingDTO> offeringList) {
        this.classDetail = classDetail;
        this.creator = creator;
        this.sameCategoryList = sameCategoryList;
        this.curriculumList = curriculumList;
        this.reviewList = reviewList;
        this.reviewSummary = reviewSummary;
        this.representativeSchedule = representativeSchedule;
        this.scheduleList = scheduleList;
        this.materialList = materialList;
        this.offeringList = offeringList;
    }
    
}