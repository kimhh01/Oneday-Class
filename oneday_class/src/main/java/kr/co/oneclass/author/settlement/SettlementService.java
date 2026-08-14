package kr.co.oneclass.author.settlement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;

@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    private final SettlementDAO sDAO;
    private final LocalFileStorageService fileStorageService;

    public SettlementService(SettlementDAO sDAO, LocalFileStorageService fileStorageService) {
        this.sDAO = sDAO;
        this.fileStorageService = fileStorageService;
    }

    // 작가의 누적 결제금액, 취소금액과 정산 예정금액을 조회한다
    public SalesSummaryDTO getSalesSummary(long authorCode) {
        SalesSummaryDTO summary = sDAO.selectSalesSummary(authorCode);
        return summary == null ? new SalesSummaryDTO() : summary;
    }

    // 결제상태, 기간, 검색어와 페이지 조건에 맞는 매출 목록을 조회한다
    public List<SalesListDTO> getSalesList(SalesSearchDTO searchDTO) {
        return sDAO.selectSalesList(searchDTO);
    }

    // 현재 검색 조건에 해당하는 전체 매출 개수를 조회한다
    public int getSalesCount(SalesSearchDTO searchDTO) {
        return sDAO.selectSalesCount(searchDTO);
    }

    // 현재 작가의 클래스 결제인지 확인한 후 결제 상세정보를 조회한다
    public SalesDetailDTO getSalesDetail(long authorCode, int paymentCode) {
        return sDAO.selectSalesDetail(authorCode, paymentCode);
    }

    // 정산 가능금액과 아직 정산 가능일이 되지 않은 대기금액을 조회한다
    public SettlementDashboardDTO getSettlementDashboard(long authorCode) {
        SettlementDashboardDTO dashboard = sDAO.selectSettlementDashboard(authorCode);
        return dashboard == null ? new SettlementDashboardDTO() : dashboard;
    }

    // 작가가 등록한 정산계좌와 통장사본 정보를 조회한다
    public SettlementAccountDTO getSettlementAccount(long authorCode) {
        SettlementAccountDTO account = sDAO.selectSettlementAccount(authorCode);
        if (account == null) {
            account = new SettlementAccountDTO();
            account.setAuthorCode(authorCode);
        }
        return account;
    }

    // 통장사본을 저장하고 CREATOR 의 정산계좌 정보를 등록하거나 수정한다
    @Transactional
    public boolean modifySettlementAccount(SettlementAccountDTO accountDTO, MultipartFile bankbookFile) {
        String accountNumber = trimToNull(accountDTO.getAccountNumber());
        if (accountNumber == null) {
            throw new IllegalArgumentException("계좌번호를 입력해주세요.");
        }
        if (accountNumber.length() > 20) {
            throw new IllegalArgumentException("계좌번호는 20자 이내로 입력해주세요.");
        }
        accountDTO.setAccountNumber(accountNumber);

        SettlementAccountDTO existing = sDAO.selectSettlementAccount(accountDTO.getAuthorCode());
        if (existing == null) {
            throw new IllegalArgumentException("작가 정보를 찾을 수 없습니다.");
        }

        String storedPath = null;
        if (bankbookFile != null && !bankbookFile.isEmpty()) {
            storedPath = fileStorageService.store(bankbookFile, "settlement-account");
            accountDTO.setBankbookPath(storedPath);
        } else if (trimToNull(existing.getBankbookPath()) == null) {
            throw new IllegalArgumentException("통장 사본을 등록해주세요.");
        }

        try {
            boolean firstRegistration = trimToNull(existing.getAccountNumber()) == null;
            int updated = firstRegistration
                    ? sDAO.insertSettlementAccount(accountDTO)
                    : sDAO.updateSettlementAccount(accountDTO);

            if (updated != 1) {
                if (storedPath != null) {
                    fileStorageService.delete(storedPath);
                }
                return false;
            }

            if (storedPath != null && existing.getBankbookPath() != null
                    && !storedPath.equals(existing.getBankbookPath())) {
                try {
                    fileStorageService.delete(existing.getBankbookPath());
                } catch (RuntimeException cleanupException) {
                    log.warn("기존 통장 사본 파일을 정리하지 못했습니다: {}", existing.getBankbookPath(), cleanupException);
                }
            }
            return true;
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

    // 아직 정산 신청되지 않은 정산 가능 매출 목록을 조회한다
    public List<SettlementTargetDTO> getSettlementTargetList(long authorCode) {
        return sDAO.selectSettlementTargetList(authorCode);
    }

    // 사용자가 선택한 매출이 실제 정산 가능한 상태인지 확인하여 다시 조회한다
    public List<SettlementTargetDTO> getSelectedSettlementTargetList(long authorCode,
            List<Integer> paymentCodeList) {
        List<Integer> distinctCodes = normalizePaymentCodes(paymentCodeList);
        if (distinctCodes.isEmpty()) {
            return new ArrayList<>();
        }
        return sDAO.selectSelectedSettlementTargetList(authorCode, distinctCodes);
    }

    // 선택 매출을 DB에서 다시 조회하여 결제금액, 수수료와 지급액을 계산한다
    public SettlementApplyDTO calculateSettlement(SettlementApplyDTO applyDTO) {
        List<Integer> requestedCodes = normalizePaymentCodes(applyDTO.getPaymentCodeList());
        if (requestedCodes.isEmpty()) {
            throw new IllegalArgumentException("정산할 매출을 한 건 이상 선택해주세요.");
        }

        List<SettlementTargetDTO> targets = sDAO.selectSelectedSettlementTargetList(
                applyDTO.getAuthorCode(), requestedCodes);
        if (targets.size() != requestedCodes.size()) {
            throw new IllegalArgumentException("정산할 수 없거나 다른 작가의 매출이 포함되어 있습니다.");
        }

        int totalPaymentAmount = 0;
        int totalFeeAmount = 0;
        int settlementAmount = 0;
        List<Integer> verifiedCodes = new ArrayList<>();
        for (SettlementTargetDTO target : targets) {
            verifiedCodes.add(target.getPaymentCode());
            totalPaymentAmount += target.getPaymentAmount();
            totalFeeAmount += target.getFeeAmount();
            settlementAmount += target.getSettlementAmount();
        }

        applyDTO.setPaymentCodeList(verifiedCodes);
        applyDTO.setTotalPaymentAmount(totalPaymentAmount);
        applyDTO.setTotalFeeAmount(totalFeeAmount);
        applyDTO.setSettlementAmount(settlementAmount);
        applyDTO.setSettlementStatus("정산대기");
        return applyDTO;
    }

    // 계좌와 매출상태를 검증하고 결제별 CALCULATE 행을 한 그룹으로 등록한다
    @Transactional
    public int submitSettlement(SettlementApplyDTO applyDTO) {
        calculateSettlement(applyDTO);

        SettlementAccountDTO account = sDAO.selectSettlementAccount(applyDTO.getAuthorCode());
        if (account == null || trimToNull(account.getAccountNumber()) == null) {
            throw new IllegalStateException("정산 계좌를 먼저 등록해주세요.");
        }
        if (trimToNull(account.getBankbookPath()) == null) {
            throw new IllegalStateException("통장 사본을 먼저 등록해주세요.");
        }

        applyDTO.setBusinessName(account.getBusinessName());
        applyDTO.setAuthorName(account.getAuthorName());
        applyDTO.setBankName(account.getBankName());
        applyDTO.setAccountNumber(account.getAccountNumber());

        int inserted = sDAO.insertSettlementTargetList(applyDTO);
        if (inserted != applyDTO.getPaymentCodeList().size()) {
            throw new IllegalStateException("정산 신청 중 매출 상태가 변경되었습니다. 다시 확인해주세요.");
        }
        return applyDTO.getSettlementCode();
    }

    // 기간, 상태와 페이지 조건에 맞는 정산 내역을 조회한다
    public List<SettlementListDTO> getSettlementList(SettlementSearchDTO searchDTO) {
        return sDAO.selectSettlementList(searchDTO);
    }

    // 현재 검색 조건에 해당하는 전체 정산 내역 개수를 조회한다
    public int getSettlementCount(SettlementSearchDTO searchDTO) {
        return sDAO.selectSettlementCount(searchDTO);
    }

    // 현재 작가의 정산인지 확인하고 정산 상세와 포함 매출 목록을 조회한다
    public SettlementDetailDTO getSettlementDetail(long authorCode, int settlementCode) {
        SettlementDetailDTO detail = sDAO.selectSettlementDetail(authorCode, settlementCode);
        if (detail != null) {
            detail.setTargetList(sDAO.selectSettlementDetailTargetList(settlementCode));
        }
        return detail;
    }

    private List<Integer> normalizePaymentCodes(List<Integer> paymentCodeList) {
        if (paymentCodeList == null) {
            return new ArrayList<>();
        }
        LinkedHashSet<Integer> uniqueCodes = new LinkedHashSet<>();
        for (Integer paymentCode : paymentCodeList) {
            if (paymentCode != null && paymentCode > 0) {
                uniqueCodes.add(paymentCode);
            }
        }
        return new ArrayList<>(uniqueCodes);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
