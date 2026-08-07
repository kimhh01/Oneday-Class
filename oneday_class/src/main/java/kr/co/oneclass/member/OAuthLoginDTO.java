package kr.co.oneclass.member;

import lombok.Data;

/**
 * 구글 등 OAuth 로그인/가입 요청 정보.
 * 다이어그램에는 oauthProviderId만 있었지만, 실제 로그인/가입 처리를 위해
 * email, name, provider(제공자 구분)를 추가했습니다.
 */
@Data
public class OAuthLoginDTO {
	private int memberCode;
    private String oauthProviderId; // 구글에서 내려주는 고유 ID (sub 값 등)
    private String email;
    private String name;
    private String provider;        // "google" 등

}
