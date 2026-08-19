package kr.co.oneclass.classDetail;

import java.sql.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewDTO {
	private int reviewCode;//리뷰코드
	private int classCode;//클래스코드
	private int starRating;//별점
	private String userName;//작성자 이름
	private String userImg;//작성자 사진
	private String content;//리뷰내용
	private List<ReviewImgDTO> reviewImg;
	private String inputDate;//리뷰작성일
	private String operatorName;//작가 이름
	private String operatorImg;//작가 사진
	private String replyContent;//답변내용
	private Date replyDate;//답변일
}
