package kr.co.oneclass.author.inquiry.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.inquiry.dto.InquiryDetailDTO;
import kr.co.oneclass.author.inquiry.dto.InquiryFormDTO;
import kr.co.oneclass.author.inquiry.dto.InquiryListDTO;
import kr.co.oneclass.author.inquiry.dto.InquirySearchDTO;
import kr.co.oneclass.author.inquiry.dto.InquirySummaryDTO;
import kr.co.oneclass.author.inquiry.dto.InquiryTypeDTO;

@Repository
public class InquiryDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.inquiry.dao.InquiryDAO.";

    private final SqlSessionTemplate sqlSession;

    public InquiryDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public InquirySummaryDTO selectInquirySummary(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectInquirySummary", authorCode);
    }

    public List<InquiryTypeDTO> selectInquiryTypeList() {
        return sqlSession.selectList(NAMESPACE + "selectInquiryTypeList");
    }

    public List<InquiryListDTO> selectInquiryList(InquirySearchDTO searchDTO) {
        return sqlSession.selectList(NAMESPACE + "selectInquiryList", searchDTO);
    }

    public InquiryDetailDTO selectInquiryDetail(long authorCode, int inquiryCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("inquiryCode", inquiryCode);
        return sqlSession.selectOne(NAMESPACE + "selectInquiryDetail", param);
    }

    public int insertInquiry(InquiryFormDTO formDTO) {
        return sqlSession.insert(NAMESPACE + "insertInquiry", formDTO);
    }
}
