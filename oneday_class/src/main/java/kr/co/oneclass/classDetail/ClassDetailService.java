package kr.co.oneclass.classDetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.bookmark.BookmarkDAO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;

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

		// 2. 클래스 정보에서 얻은 creatorCode(또는 operatorCode)와 categoryCode 추출
		// DTO 내부 변수명에 맞게 호출해주세요 (예: getCreatorCode, getCategoryCode)
		long creatorCode = classDto.getOperatorCode();
		int categoryCode = classDto.getCategoryCode();

		// 3. 추가 정보들을 DAO를 통해 조회
		OperatorDTO creatorDto = classDetailDAO.selectCreator(creatorCode);
		List<ClassDTO> sameCategoryList = classDetailDAO.selectSameCategoryList(classCode, categoryCode);
		List<CurriculumDTO> curriculumList = classDetailDAO.selectCurriculum(classCode);
		List<ReviewDTO> reviewList = classDetailDAO.selectReviewList(classCode);
		ReviewSummaryDTO reviewSummaryDto = classDetailDAO.selectReviewSummary(classCode);
		List<ScheduleDTO> representativeSchedule = classDetailDAO.selectSchedule(classCode);
		List<ScheduleDTO> scheduleList = classDetailDAO.selectScheduleList(classCode);

		// 4. 하나의 DTO로 결합하여 반환
		return new ClassDetailResponseDTO(classDto, creatorDto, sameCategoryList, curriculumList, reviewList,
				reviewSummaryDto, representativeSchedule, scheduleList);
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