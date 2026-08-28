package kr.co.oneclass.author.classmanage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class TossPaymentCancellationClientTest {

    @Test
    void dummySecretKeyIsRejectedBeforeCallingToss() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        TossPaymentCancellationClient client =
                new TossPaymentCancellationClient(restTemplate, "dummy");

        assertThrows(IllegalArgumentException.class,
                () -> client.cancelFullPayment("payment-key", 10, 20));

        verifyNoInteractions(restTemplate);
    }
}
