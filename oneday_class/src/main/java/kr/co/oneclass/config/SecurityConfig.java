package kr.co.oneclass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 화면(퍼블리싱) 확인용 임시 보안 설정.
 *
 * spring-boot-starter-security 가 클래스패스에 있으면 기본적으로 모든 요청이
 * 로그인 화면으로 막히기 때문에, 정적 화면을 확인할 수 있도록 전부 열어둔다.
 *
 * TODO 로그인/권한 기능 붙일 때 아래 permitAll 을 실제 정책으로 교체할 것.
 *      (예: /author/** 는 ROLE_AUTHOR 만 접근)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.anyRequest().permitAll()
			)
			// 기본 로그인 폼/베이직 인증 비활성화 (아직 로그인 기능 없음)
			.formLogin(FormLoginConfigurer::disable)
			.httpBasic(HttpBasicConfigurer::disable);

		// CSRF 는 기본값(활성) 유지.
		// 나중에 POST 폼을 만들 때 hidden 으로 _csrf 토큰을 같이 보내면 된다.
		return http.build();
	}
}
