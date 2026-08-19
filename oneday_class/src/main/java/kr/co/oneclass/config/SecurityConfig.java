package kr.co.oneclass.config;

import kr.co.oneclass.member.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 활성화
            .csrf(csrf -> csrf
            		.ignoringRequestMatchers("/member/check-id") // 아이디 중복 확인 URL 패턴 추가
            		//.disable())
            		)
            // 2. 모든 요청에 대해 접근 전체 허용 (로그인 없이 모든 페이지/정적 자원 접근 가능)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // 3. OAuth2 로그인 기능은 유지 (사용자가 직접 구글 로그인 버튼 클릭 시 작동)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/member/login")
                .redirectionEndpoint(redirection -> redirection
                    .baseUri("/login/oauth2/*")
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .defaultSuccessUrl("/", true)
            );

        return http.build();
    }
}