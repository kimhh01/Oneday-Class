package kr.co.oneclass.classDetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import kr.co.oneclass.bookmark.BookmarkDAO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ClassImageDTO;
import kr.co.oneclass.common.ScheduleDTO;
import kr.co.oneclass.common.TagDTO;

@Service
public class ClassDetailService {

	@Autowired
	private ClassDetailDAO classDetailDAO;

	
	@Autowired 
	private BookmarkDAO bDAO;
	

	public ClassDetailResponseDTO getClassDetail(int classCode) {
		// 1. 클래스 기본 상세 조회
		ClassDTO classDto = classDetailDAO.selectClass(classCode);
		if (classDto == null) {
			return null;
		}
		
		if(!"모집중".equals(classDto.getStatus())) {
			return null;
		}
		
		
		// 💡 [추가] 이미지 리스트 조회 후 ClassDTO에 넣어주기!
	    List<ClassImageDTO> imageList = classDetailDAO.selectClassImageList(classCode);
	    classDto.setImageList(imageList);
		
	 // 2. 기존 로직 동일
	    long creatorCode = classDto.getOperatorCode();
	    int categoryCode = classDto.getCategoryCode();

	    OperatorDTO creatorDto = classDetailDAO.selectCreator(creatorCode);
	    List<ClassDTO> sameCategoryList = classDetailDAO.selectSameCategoryList(classCode, categoryCode);
	    List<CurriculumDTO> curriculumList = classDetailDAO.selectCurriculum(classCode);
	    List<ReviewDTO> reviewList = classDetailDAO.selectReviewList(classCode);
	    // 2. 리뷰 리스트를 반복문 돌리며 각 reviewCode로 이미지를 조회해 세팅합니다.
	    if (reviewList != null && !reviewList.isEmpty()) {
	        for (ReviewDTO review : reviewList) {
	            int reviewCode = review.getReviewCode(); // 리뷰 DTO에서 reviewCode 추출
	            List<ReviewImgDTO> imgList = classDetailDAO.selectReviewImgList(reviewCode);
	            review.setReviewImg(imgList); // 리뷰 DTO 내부의 List<ReviewImgDTO>에 세팅
	        }
	    }
	    ReviewSummaryDTO reviewSummaryDto = classDetailDAO.selectReviewSummary(classCode);
	    List<ScheduleDTO> representativeSchedule = classDetailDAO.selectSchedule(classCode);
	    List<ScheduleDTO> scheduleList = classDetailDAO.selectScheduleList(classCode);
	    List<MaterialDTO> materialList = classDetailDAO.selectMaterialList(classCode);
	    List<OfferingDTO> offeringList = classDetailDAO.selectOfferingList(classCode);
	    List<TagDTO> tagList = classDetailDAO.selectTagList(classCode);
	    List<DetailInfoDTO> detailInfoList = classDetailDAO.selectDetailInfoList(classCode);
	    List<AdditionalInfoDTO> additionalInfoList = classDetailDAO.selectAdditionalInfo(classCode);
	    
		// 4. 하나의 DTO로 결합하여 반환
		return new ClassDetailResponseDTO(classDto, creatorDto, sameCategoryList, curriculumList, reviewList,
				reviewSummaryDto, representativeSchedule, scheduleList, materialList,
				offeringList, tagList, detailInfoList, additionalInfoList);
	}
	
	

	//북마크 추가 or 제거
	 @Transactional
	 public boolean addBookmark(String memberCode, String classCode) {
		 int count=bDAO.checkBookmark(memberCode, classCode);
				 
		 if(count > 0) {
			 bDAO.deleteBookmark(memberCode , classCode);
			 return false; // DB에서 삭제됨 (REMOVED)
		 } else {
			 bDAO.insertBookmark(memberCode, classCode);
		 }	
		 return true;
	 }
	 
	
}