package kr.co.oneclass.author.settlement;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class SettlementDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.settlement.SettlementDAO.";

    private final SqlSessionTemplate sqlSession;

    public SettlementDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 작가별 누적 결제금액, 취소금액과 정산 예정금액을 집계한다
    public SalesSummaryDTO selectSalesSummary(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectSalesSummary", authorCode);
    }

    // 매출 검색, 필터, 정렬과 페이지 조건에 맞는 결제 목록을 조회한다
    public List<SalesListDTO> selectSalesList(SalesSearchDTO searchDTO) {
        return sqlSession.selectList(NAMESPACE + "selectSalesList", searchDTO);
    }

    // 검색 조건에 해당하는 전체 결제 개수를 조회한다
    public int selectSalesCount(SalesSearchDTO searchDTO) {
        return sqlSession.selectOne(NAMESPACE + "selectSalesCount", searchDTO);
    }

    // 작가 코드와 결제 코드에 해당하는 결제 상세정보를 조회한다
    public SalesDetailDTO selectSalesDetail(long authorCode, int paymentCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("paymentCode", paymentCode);
        return sqlSession.selectOne(NAMESPACE + "selectSalesDetail", param);
    }

    // 정산 가능금액, 정산 대기금액과 신청 진행금액을 집계한다
    public SettlementDashboardDTO selectSettlementDashboard(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectSettlementDashboard", authorCode);
    }

    // 작가의 정산계좌와 통장사본 경로를 조회한다
    public SettlementAccountDTO selectSettlementAccount(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectSettlementAccount", authorCode);
    }

    // 최초 정산계좌 정보를 등록한다
    public int insertSettlementAccount(SettlementAccountDTO accountDTO) {
        return sqlSession.update(NAMESPACE + "insertSettlementAccount", accountDTO);
    }

    // 기존 정산계좌와 통장사본 경로를 수정한다
    public int updateSettlementAccount(SettlementAccountDTO accountDTO) {
        return sqlSession.update(NAMESPACE + "updateSettlementAccount", accountDTO);
    }

    // 정산 가능한 미신청 매출 목록을 조회한다
    public List<SettlementTargetDTO> selectSettlementTargetList(long authorCode) {
        return sqlSession.selectList(NAMESPACE + "selectSettlementTargetList", authorCode);
    }

    // 사용자가 선택한 결제 코드에 해당하는 정산 가능 매출을 조회한다
    public List<SettlementTargetDTO> selectSelectedSettlementTargetList(long authorCode,
            List<Integer> paymentCodeList) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("paymentCodeList", paymentCodeList);
        return sqlSession.selectList(NAMESPACE + "selectSelectedSettlementTargetList", param);
    }

    // 선택한 결제마다 CALCULATE 행을 만들고 같은 그룹 코드로 묶는다
    public int insertSettlementTargetList(SettlementApplyDTO applyDTO) {
        return sqlSession.insert(NAMESPACE + "insertSettlementTargetList", applyDTO);
    }

    // 정산 내역 목록을 기간과 상태 조건에 맞게 조회한다
    public List<SettlementListDTO> selectSettlementList(SettlementSearchDTO searchDTO) {
        return sqlSession.selectList(NAMESPACE + "selectSettlementList", searchDTO);
    }

    // 검색 조건에 해당하는 전체 정산 내역 개수를 조회한다
    public int selectSettlementCount(SettlementSearchDTO searchDTO) {
        return sqlSession.selectOne(NAMESPACE + "selectSettlementCount", searchDTO);
    }

    // 정산 신청금액, 지급계좌, 상태와 처리일을 조회한다
    public SettlementDetailDTO selectSettlementDetail(long authorCode, int settlementCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("authorCode", authorCode);
        param.put("settlementCode", settlementCode);
        return sqlSession.selectOne(NAMESPACE + "selectSettlementDetail", param);
    }

    // 해당 정산에 포함된 개별 매출 목록을 조회한다
    public List<SettlementTargetDTO> selectSettlementDetailTargetList(int settlementCode) {
        return sqlSession.selectList(NAMESPACE + "selectSettlementDetailTargetList", settlementCode);
    }
}
