package kr.co.oneclass.review;

import org.springframework.beans.factory.annotation.Autowired;
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
                String uploadDir = System.getProperty("user.dir") + "/uploads/review/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String savedFilename = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                        File dest = new File(uploadDir + savedFilename);
                        image.transferTo(dest);

                        String imagePath = "/uploads/review/" + savedFilename;

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
    public Review getReview(int classCode, int memberCode) {
        return reviewDAO.selectReview(classCode, memberCode);
    }
}