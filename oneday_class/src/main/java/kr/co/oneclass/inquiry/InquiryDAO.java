package kr.co.oneclass.inquiry;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InquiryDAO {

    /**
     * 0. 문의 유형 목록 전체 조회 (등록 폼용)
     */
    List<InquiryDTO> selectInquiryTypeList();

    /**
     * 1. 회원별 문의 목록 조회 (카테고리 필터링)
     */
    List<InquiryDTO> selectListByMember(@Param("memberCode") String memberCode, @Param("type") String type);

    /**
     * 2. 문의 내역 상세 조회
     */
    InquiryDTO selectDetail(@Param("inquiryCode") String inquiryCode);

    /**
     * 3. 신규 문의 등록
     */
    int insertInquiry(InquiryDTO idto);
}