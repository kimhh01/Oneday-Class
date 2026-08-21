package kr.co.oneclass.author.settlement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.oneclass.author.common.LocalFileStorageService;

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
}
