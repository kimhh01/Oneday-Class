package kr.co.oneclass.classDetail;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewSummaryDTO {
	private int classCode;//클래스코드
	private double averageRating; // 평균 평점
	private int totalReviewCount; // 총 리뷰 수
	private double fiveStarCount; 
	private double fourStarCount;
	private double threeStarCount;
	private double twoStarCount;
	private double oneStarCount;
}
