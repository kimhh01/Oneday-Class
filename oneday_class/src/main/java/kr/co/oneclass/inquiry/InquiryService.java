package kr.co.oneclass.inquiry;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface InquiryService {

    /**
     * 0. 문의 유형 목록 전체 조회
     */
    List<InquiryDTO> getInquiryTypeList();

    /**
     * 1. 로그인 회원의 문의 목록 조회 (카테고리 필터링)
     */
    List<InquiryDTO> getInquiryList(String memberCode, String type);

    /**
     * 2. 문의 내역 상세 조회
     */
    InquiryDTO getInquiryDetail(String inquiryCode, String memberCode);

    /**
     * 3. 신규 문의 등록 (첨부파일 업로드 포함)
     */
    boolean writeInquiry(InquiryDTO idto, MultipartFile file);
}