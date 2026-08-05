package kr.co.oneclass.config;

import kr.co.oneclass.member.service.CustomOAuth2UserService;
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
            // 1. 개발 편의를 위한 CSRF 비활성화
            .csrf(csrf -> csrf.disable())

            // 2. 권한 설정 (로그인 선택 화면, 회원가입, 아이디/비밀번호 찾기 및 static 자원은 무조건 허용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/member/**", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 3. OAuth2 로그인 커스텀 설정
            .oauth2Login(oauth2 -> oauth2
            	    .loginPage("/member/login")
            	    // 스프링 시큐리티가 리다이렉트 요청을 수신할 엔드포인트 세팅
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