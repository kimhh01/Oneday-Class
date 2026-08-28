package kr.co.oneclass.author.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.oneclass.author.common.LocalFileStorageService;
import kr.co.oneclass.common.AESUtil;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private SettlementDAO settlementDAO;
    @Mock
    private LocalFileStorageService fileStorageService;

    private SettlementService settlementService;

    @BeforeEach
    void setUp() {
        settlementService = new SettlementService(settlementDAO, fileStorageService);
    }

    @Test
    void settlementSubmissionRequiresServerSideAgreement() {
        SettlementApplyDTO form = new SettlementApplyDTO();
        form.setAuthorCode(1L);
        form.getPaymentCodeList().add(10);

        assertThrows(IllegalArgumentException.class,
                () -> settlementService.submitSettlement(form));
        verifyNoInteractions(settlementDAO);
    }

    @Test
    void customerNamesAndEmailAreDecryptedForSalesScreens() {
        SalesListDTO sale = new SalesListDTO();
        sale.setMemberName(AESUtil.encrypt("김구매"));
        SalesDetailDTO detail = new SalesDetailDTO();
        detail.setMemberName(AESUtil.encrypt("박구매"));
        detail.setMemberEmail(AESUtil.encrypt("buyer@example.com"));
        SalesSearchDTO search = new SalesSearchDTO();

        when(settlementDAO.selectSalesList(search)).thenReturn(List.of(sale));
        when(settlementDAO.selectSalesDetail(7L, 10)).thenReturn(detail);

        assertEquals("김구매", settlementService.getSalesList(search).get(0).getMemberName());
        SalesDetailDTO result = settlementService.getSalesDetail(7L, 10);
        assertEquals("박구매", result.getMemberName());
        assertEquals("buyer@example.com", result.getMemberEmail());
    }

    @Test
    void accountHolderIsDecryptedForSettlementScreens() {
        SettlementAccountDTO account = new SettlementAccountDTO();
        account.setAuthorName(AESUtil.encrypt("김작가"));
        account.setAccountNumber("1234567890");
        SettlementDetailDTO detail = new SettlementDetailDTO();
        detail.setAuthorName(AESUtil.encrypt("김작가"));

        when(settlementDAO.selectSettlementAccount(7L)).thenReturn(account);
        when(settlementDAO.selectSettlementDetail(7L, 20)).thenReturn(detail);
        when(settlementDAO.selectSettlementDetailTargetList(20)).thenReturn(List.of());

        assertEquals("김작가", settlementService.getSettlementAccount(7L).getAuthorName());
        assertEquals("김작가", settlementService.getSettlementDetail(7L, 20).getAuthorName());
    }

    @Test
    void legacyPlaintextCustomerNameRemainsUnchanged() {
        SalesListDTO sale = new SalesListDTO();
        sale.setMemberName("기존회원");
        SalesSearchDTO search = new SalesSearchDTO();
        when(settlementDAO.selectSalesList(search)).thenReturn(List.of(sale));

        assertEquals("기존회원", settlementService.getSalesList(search).get(0).getMemberName());
    }
}
