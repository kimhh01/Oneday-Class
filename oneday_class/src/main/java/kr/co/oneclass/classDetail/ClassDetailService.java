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
	private ClassDetailDAO cDAO;

	
	@Autowired 
	private BookmarkDAO bDAO;
	

	public ClassDetailResponseDTO getClassDetail(int classCode) {
		// 1. 클래스 기본 상세 조회
		ClassDTO classDto = cDAO.selectClass(classCode);
		if (classDto == null) {
			return null;
		}
		
		if(!"모집중".equals(classDto.getStatus())) {
			return null;
		}
		
		
		// 💡 [추가] 이미지 리스트 조회 후 ClassDTO에 넣어주기!
	    List<ClassImageDTO> imageList = cDAO.selectClassImageList(classCode);
	    classDto.setImageList(imageList);
		
	 // 2. 기존 로직 동일
	    long creatorCode = classDto.getOperatorCode();
	    int categoryCode = classDto.getCategoryCode();

	    OperatorDTO creatorDto = cDAO.selectCreator(creatorCode);
	    
	    List<ClassDTO> sameCategoryList = cDAO.selectSameCategoryList(classCode, categoryCode);

	    if (sameCategoryList != null && !sameCategoryList.isEmpty()) {
	        for (ClassDTO item : sameCategoryList) {
	            List<ClassImageDTO> imgList = cDAO.selectClassImageList(item.getClassCode());
	            item.setImageList(imgList);
	        }
	    }
	    List<CurriculumDTO> curriculumList = cDAO.selectCurriculum(classCode);
	    List<ReviewDTO> reviewList = cDAO.selectReviewList(classCode);
	    // 2. 리뷰 리스트를 반복문 돌리며 각 reviewCode로 이미지를 조회해 세팅합니다.
	    if (reviewList != null && !reviewList.isEmpty()) {
	        for (ReviewDTO review : reviewList) {
	            int reviewCode = review.getReviewCode(); // 리뷰 DTO에서 reviewCode 추출
	            List<ReviewImgDTO> imgList = cDAO.selectReviewImgList(reviewCode);
	            review.setReviewImg(imgList); // 리뷰 DTO 내부의 List<ReviewImgDTO>에 세팅
	        }
	    }
	    ReviewSummaryDTO reviewSummaryDto = cDAO.selectReviewSummary(classCode);
	    List<ScheduleDTO> representativeSchedule = cDAO.selectSchedule(classCode);
	    List<ScheduleDTO> scheduleList = cDAO.selectScheduleList(classCode);
	    List<MaterialDTO> materialList = cDAO.selectMaterialList(classCode);
	    List<OfferingDTO> offeringList = cDAO.selectOfferingList(classCode);
	    List<TagDTO> tagList = cDAO.selectTagList(classCode);
	    List<DetailInfoDTO> detailInfoList = cDAO.selectDetailInfoList(classCode);
	    List<AdditionalInfoDTO> additionalInfoList = cDAO.selectAdditionalInfo(classCode);
	    
		// 4. 하나의 DTO로 결합하여 반환
		return new ClassDetailResponseDTO(classDto, creatorDto, sameCategoryList, curriculumList, reviewList,
				reviewSummaryDto, representativeSchedule, scheduleList, materialList,
				offeringList, tagList, detailInfoList, additionalInfoList);
	}
	//조회수 증가 메서드
	public void increaseViewCount(int classCode) {
		cDAO.increaseViewCount(classCode);
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