package kr.co.oneclass.inquiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    private InquiryDAO id;

    // 💡 properties의 /app/upload/ 경로 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public List<InquiryDTO> getInquiryTypeList() {
        return id.selectInquiryTypeList();
    }

    @Override
    public List<InquiryDTO> getInquiryList(String memberCode, String type) {
        return id.selectListByMember(memberCode, type);
    }

    @Override
    public InquiryDTO getInquiryDetail(String inquiryCode, String memberCode) {
        return id.selectDetail(inquiryCode);
    }

    @Override
    public boolean writeInquiry(InquiryDTO idto, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                // 1. 실제 파일이 저장될 물리 경로 (/app/upload/inquiry/)
                String saveDirPath = uploadDir + "inquiry/";
                File dir = new File(saveDirPath);
                if (!dir.exists()) {
                    dir.mkdirs(); // 디렉터리가 없으면 생성
                }

                String originalFilename = file.getOriginalFilename();
                String saveFilename = UUID.randomUUID().toString() + "_" + originalFilename;

                File dest = new File(saveDirPath + saveFilename);
                file.transferTo(dest);

                // 2. DB에 저장될 웹 접근 URL 경로 (WebConfig의 /upload/** 와 매칭)
                idto.setInquiryImg("/upload/inquiry/" + saveFilename);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        int result = id.insertInquiry(idto);
        return result > 0;
    }
}