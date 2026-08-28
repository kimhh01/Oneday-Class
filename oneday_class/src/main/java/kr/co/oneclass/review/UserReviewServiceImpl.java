package kr.co.oneclass.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 💡 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UserReviewServiceImpl implements UserReviewService {

    @Autowired
    private UserReviewDAO reviewDAO;

    // 💡 properties의 /app/upload/ 경로 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public boolean writeReview(ReviewDTO rdto) {
        try {
            // 1. 리뷰 본문 INSERT (MyBatis selectKey에 의해 rdto.reviewCode에 PK가 자동 설정됨)
            int result = reviewDAO.insertReview(rdto);
            if (result <= 0) return false;

            // 2. DTO 내부에서 이미지 파일 리스트 가져오기
            List<MultipartFile> images = rdto.getImages();

            // 3. 다중 이미지 파일 업로드 및 DB 저장
            if (images != null && !images.isEmpty()) {
                // 💡 실제 파일이 저장될 물리 경로 (/app/upload/review/)
                String saveDirPath = uploadDir + "review/";
                File dir = new File(saveDirPath);
                if (!dir.exists()) {
                    dir.mkdirs(); // 디렉터리가 없으면 생성
                }

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String savedFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        File dest = new File(saveDirPath + savedFilename);
                        image.transferTo(dest);

                        // 💡 DB에 저장될 웹 접근 URL 경로 (WebConfig의 /upload/** 와 매칭)
                        String imagePath = "/upload/review/" + savedFilename;

                        // 생성된 reviewCode와 이미지 경로를 테이블에 각각 저장
                        reviewDAO.insertReviewImg(rdto.getReviewCode(), imagePath);
                    }
                }
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Review getReview(int reservationCode) {
        return reviewDAO.selectReview(reservationCode);
    }
}