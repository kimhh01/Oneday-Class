package kr.co.oneclass.classDetail;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;


@Mapper
public interface ClassDetailDAO {
	
	//클래스 정보 조회
	public ClassDTO selectClass(int classCode);
	
	//비슷한 클래스 조회
	public List<ClassDTO> selectSameCategoryList( 
			@Param("classCode") int classCode,
	        @Param("categoryCode") int categoryCode
	        );
	
	//작가 조회
	public OperatorDTO selectCreator(long operatorCode);

	//클래스 스케쥴 조회
	public List<ScheduleDTO> selectSchedule(int classCode);
	
	//클래스 스케쥴 리스트 조회
	public List<ScheduleDTO> selectScheduleList(int classCode);
	
	//커리큘럼 조회
	public List<CurriculumDTO> selectCurriculum(int classCode);
	
	//리뷰 리스트 조회
	public List<ReviewDTO> selectReviewList(int classCode);
	
	//리뷰별점, 리뷰 점수 조회
	public ReviewSummaryDTO selectReviewSummary(int classCode);
	
	
}
