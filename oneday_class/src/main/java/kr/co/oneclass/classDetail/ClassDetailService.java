package kr.co.oneclass.classDetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.bookmark.BookmarkDAO;
import kr.co.oneclass.common.AESUtil; // 💡 AESUtil import 추가
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
		
		if (!"모집중".equals(classDto.getStatus())) {
			return null;
		}
		
		// 이미지 리스트 조회 후 ClassDTO에 세팅
		List<ClassImageDTO> imageList = cDAO.selectClassImageList(classCode);
		classDto.setImageList(imageList);
		
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

		// 💡 2. 리뷰 리스트 반복문에서 리뷰 이미지 세팅 및 복호화 수행
		if (reviewList != null && !reviewList.isEmpty()) {
			for (ReviewDTO review : reviewList) {
				int reviewCode = review.getReviewCode();
				List<ReviewImgDTO> imgList = cDAO.selectReviewImgList(reviewCode);
				review.setReviewImg(imgList);

				// 리뷰 본문 복호화
				if (review.getContent() != null) {
					review.setContent(AESUtil.decrypt(review.getContent()));
				}
				
				// 작성자 이름 복호화 (userName)
				if (review.getUserName() != null) {
					review.setUserName(AESUtil.decrypt(review.getUserName()));
				}
				
				// 작가 답글 내용 복호화 (replyContent)
				if (review.getReplyContent() != null) {
					review.setReplyContent(AESUtil.decrypt(review.getReplyContent()));
				}
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

	// 조회수 증가 메서드
	public void increaseViewCount(int classCode) {
		cDAO.increaseViewCount(classCode);
	}

	// 북마크 추가 or 제거
	@Transactional
	public boolean addBookmark(String memberCode, String classCode) {
		int count = bDAO.checkBookmark(memberCode, classCode);
				 
		if (count > 0) {
			bDAO.deleteBookmark(memberCode, classCode);
			return false; // DB에서 삭제됨 (REMOVED)
		} else {
			bDAO.insertBookmark(memberCode, classCode);
		}	
		return true;
	}
}