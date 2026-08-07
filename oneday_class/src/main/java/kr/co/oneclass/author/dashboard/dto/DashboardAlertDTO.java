package kr.co.oneclass.author.dashboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardAlertDTO {

    private String alertType;     // 알림 유형
    private String alertTitle;    // 알림 제목
    private String alertContent;  // 알림 내용
    private int alertCount;       // 알림 건수
    private String targetUrl;     // 알림 클릭 시 이동할 경로
}
