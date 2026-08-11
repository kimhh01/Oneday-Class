package kr.co.oneclass.inquiry;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final String uploadDir = System.getProperty("user.dir").replace("\\", "/") + "/uploads/inquiry/";
    
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
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String originalFilename = file.getOriginalFilename();
                String saveFilename = UUID.randomUUID().toString() + "_" + originalFilename;

                File dest = new File(uploadDir + saveFilename);
                file.transferTo(dest);

                idto.setInquiryImg("/uploads/inquiry/" + saveFilename);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        int result = id.insertInquiry(idto);
        return result > 0;
    }
}