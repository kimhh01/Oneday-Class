package kr.co.oneclass.author.inquiry;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;

@Service
public class InquiryService {

    private final InquiryDAO inquiryDAO;
    private final LocalFileStorageService fileStorageService;

    public InquiryService(InquiryDAO inquiryDAO, LocalFileStorageService fileStorageService) {
        this.inquiryDAO = inquiryDAO;
        this.fileStorageService = fileStorageService;
    }

    public InquirySummaryDTO getInquirySummary(long authorCode) {
        InquirySummaryDTO summary = inquiryDAO.selectInquirySummary(authorCode);
        return summary == null ? new InquirySummaryDTO() : summary;
    }

    public List<InquiryTypeDTO> getInquiryTypeList() {
        return inquiryDAO.selectInquiryTypeList();
    }

    public List<InquiryListDTO> getInquiryList(InquirySearchDTO searchDTO) {
        return inquiryDAO.selectInquiryList(searchDTO);
    }

    public InquiryDetailDTO getInquiryDetail(long authorCode, int inquiryCode) {
        return inquiryDAO.selectInquiryDetail(authorCode, inquiryCode);
    }

    @Transactional
    public int addInquiry(InquiryFormDTO formDTO, MultipartFile inquiryFile) {
        validateForm(formDTO);

        boolean validType = inquiryDAO.selectInquiryTypeList().stream()
                .anyMatch(type -> type.getInquiryTypeCode() == formDTO.getInquiryTypeCode());
        if (!validType) {
            throw new IllegalArgumentException("문의 유형을 다시 선택해주세요.");
        }

        String storedPath = null;
        if (inquiryFile != null && !inquiryFile.isEmpty()) {
            storedPath = fileStorageService.store(inquiryFile, "inquiry");
            formDTO.setInquiryImg(storedPath);
        }

        try {
            int inserted = inquiryDAO.insertInquiry(formDTO);
            if (inserted != 1) {
                throw new IllegalStateException("문의를 등록하지 못했습니다.");
            }
            return formDTO.getInquiryCode();
        } catch (RuntimeException exception) {
            if (storedPath != null) {
                try {
                    fileStorageService.delete(storedPath);
                } catch (RuntimeException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw exception;
        }
    }

    private void validateForm(InquiryFormDTO formDTO) {
        String title = trimToNull(formDTO.getTitle());
        String content = trimToNull(formDTO.getContent());
        if (formDTO.getInquiryTypeCode() <= 0) {
            throw new IllegalArgumentException("문의 유형을 선택해주세요.");
        }
        if (title == null) {
            throw new IllegalArgumentException("문의 제목을 입력해주세요.");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("문의 제목은 100자 이내로 입력해주세요.");
        }
        if (content == null) {
            throw new IllegalArgumentException("문의 내용을 입력해주세요.");
        }
        if (content.length() > 500) {
            throw new IllegalArgumentException("문의 내용은 500자 이내로 입력해주세요.");
        }
        formDTO.setTitle(title);
        formDTO.setContent(content);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
