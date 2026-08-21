package kr.co.oneclass.author.classmanage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

@Service
public class TossPaymentCancellationClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentCancellationClient.class);
    private static final String CANCEL_URL = "https://api.tosspayments.com/v1/payments/%s/cancel";

    private final RestTemplate restTemplate;
    private final String secretKey;

    @Autowired
    public TossPaymentCancellationClient(@Value("${toss.secret-key:}") String secretKey) {
        this(new RestTemplate(), secretKey);
    }

    TossPaymentCancellationClient(RestTemplate restTemplate, String secretKey) {
        this.restTemplate = restTemplate;
        this.secretKey = secretKey == null ? "" : secretKey.trim();
    }

    public void validateConfiguration() {
        if (!secretKey.startsWith("test_sk_") && !secretKey.startsWith("live_sk_")) {
            throw new IllegalArgumentException(
                    "Toss 시크릿 키가 설정되지 않아 예약을 환불할 수 없습니다. 키 설정 후 다시 시도해주세요.");
        }
    }

    public void cancelFullPayment(String paymentKey, int scheduleCode, int paymentCode) {
        validateConfiguration();
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("Toss 결제 키가 없는 예약은 자동 환불할 수 없습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, "", StandardCharsets.UTF_8);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Idempotency-Key", "author-schedule-" + scheduleCode + "-payment-" + paymentCode);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(
                Map.of("cancelReason", "작가 클래스 일정 취소"), headers);
        String encodedPaymentKey = UriUtils.encodePathSegment(paymentKey.trim(), StandardCharsets.UTF_8);

        try {
            restTemplate.exchange(
                    CANCEL_URL.formatted(encodedPaymentKey),
                    HttpMethod.POST,
                    request,
                    Map.class);
        } catch (HttpStatusCodeException exception) {
            if (exception.getResponseBodyAsString().contains("ALREADY_CANCELED_PAYMENT")) {
                return;
            }
            log.warn("Toss 결제 취소 실패: paymentCode={}, status={}",
                    paymentCode, exception.getStatusCode());
            throw new IllegalArgumentException(
                    "Toss 결제 취소에 실패했습니다. 일정은 변경되지 않았습니다.", exception);
        } catch (RestClientException exception) {
            log.warn("Toss 결제 취소 통신 실패: paymentCode={}", paymentCode, exception);
            throw new IllegalArgumentException(
                    "Toss 결제 취소 서버에 연결하지 못했습니다. 일정은 변경되지 않았습니다.", exception);
        }
    }
}
