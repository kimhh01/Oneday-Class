package kr.co.oneclass.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private int reviewCode;
    private int classCode;
    private int memberCode;
    private String reviewContent;
    private Date reviewDate;
    
 // 💡 별점 데이터 바인딩을 위한 rating 필드 추가
    private double rating;

 // 💡 파일 업로드 처리를 위한 멀티파트 파일 리스트 추가
    private List<MultipartFile> images;
}