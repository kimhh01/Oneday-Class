package kr.co.oneclass.creatorDetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kr.co.oneclass.classDetail.OperatorDTO;
import kr.co.oneclass.classDetail.ReviewDTO;
import kr.co.oneclass.classDetail.ReviewImgDTO;
import kr.co.oneclass.classDetail.ReviewSummaryDTO;
import kr.co.oneclass.common.ClassDTO;

@Service
public class CreatorService {
	
	@Autowired
	private CreatorDAO cDAO;
	
	
	public OperatorDTO getCreator(long creatorCode) {
		OperatorDTO oDTO=cDAO.selectCreator(creatorCode);
		return oDTO;
	}
	
	public List<ClassDTO> getClassList(long creatorCode){
		List<ClassDTO> classList=cDAO.selectClassList(creatorCode);
		return classList;
	}
	
	public ReviewSummaryDTO getReviewSummary(long creatorCode) {
		ReviewSummaryDTO rsDTO=cDAO.selectReviewSummary(creatorCode);
		return rsDTO;
	}
	
	public List<ReviewDTO> getReviewList(long creatorCode) {
		List<ReviewDTO> reviewList=cDAO.selectReviewList(creatorCode);
		// 2. 리뷰 리스트를 반복문 돌리며 각 reviewCode로 이미지를 조회해 세팅합니다.
	    if (reviewList != null && !reviewList.isEmpty()) {
	        for (ReviewDTO review : reviewList) {
	            int reviewCode = review.getReviewCode(); // 리뷰 DTO에서 reviewCode 추출
	            List<ReviewImgDTO> imgList = cDAO.selectReviewImgList(reviewCode);
	            review.setReviewImg(imgList); // 리뷰 DTO 내부의 List<ReviewImgDTO>에 세팅
	        }
	    }
		
		
		return reviewList;
	}
	
}
