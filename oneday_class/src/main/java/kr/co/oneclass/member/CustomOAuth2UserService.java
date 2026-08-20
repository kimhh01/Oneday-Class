package kr.co.oneclass.member;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.member.OAuthLoginDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2 공급자 구분 (google)
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauthProviderId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        OAuthLoginDTO oauthdto = new OAuthLoginDTO();
        oauthdto.setProvider(provider);
        oauthdto.setOauthProviderId(oauthProviderId);
        oauthdto.setEmail(email);
        oauthdto.setName(name);

        // 💡 [핵심 추가] userRequest 객체에서 발급된 Access Token을 추출합니다.
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // 회원가입 또는 기존 회원 로그인 처리
        Member member = memberService.processOAuthLogin(oauthdto);

        // 세션에 로그인 회원 정보 저장
        if (member != null) {
            session.setAttribute("loginMember", member);
            
            // 💡 [핵심 추가] 나중에 '회원탈퇴(Revoke)' 시 사용하기 위해 세션에 토큰을 저장합니다.
            if ("GOOGLE".equals(provider)) {
                session.setAttribute("googleAccessToken", accessToken);
            }
        }

        return oAuth2User;
    }
}