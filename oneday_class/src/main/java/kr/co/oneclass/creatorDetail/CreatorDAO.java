package kr.co.oneclass.creatorDetail;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.oneclass.classDetail.OperatorDTO;
import kr.co.oneclass.classDetail.ReviewDTO;
import kr.co.oneclass.classDetail.ReviewImgDTO;
import kr.co.oneclass.classDetail.ReviewSummaryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ClassImageDTO;

@Mapper
public interface CreatorDAO {

	public OperatorDTO selectCreator(long creatorCode);
	
	public List<ClassDTO> selectClassList(long creatorCode);
	
	public List<ClassImageDTO> selectClassImgList(
			@Param("creatorCode") long creatorCode, 
		    @Param("classCode") int classCode);
	
	public ReviewSummaryDTO selectReviewSummary(long creatorCode);
	
	public List<ReviewDTO> selectReviewList(long creatorCode);
	
	public List<ReviewImgDTO> selectReviewImgList(int reviewCode);
}
