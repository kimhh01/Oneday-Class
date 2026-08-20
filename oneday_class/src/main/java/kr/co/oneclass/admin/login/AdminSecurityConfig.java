package kr.co.oneclass.admin.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AdminSecurityConfig {

	@Bean
	@Order(1) // 💡 1순위 검사: /admin 경로만 먼저 가로채서 처리
	SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {

		http
			// 💡 핵심: /admin으로 시작하는 모든 요청에만 이 시큐리티 체인을 적용합니다.
			.securityMatcher("/admin/**")
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/admin/login", "/admin/css/**", "/admin/js/**", "/admin/images/**")
				.permitAll()
				.anyRequest()
				.hasRole("ADMIN")
			)
			.formLogin(form -> form
				.loginPage("/admin/login")
				.loginProcessingUrl("/admin/login")
				.usernameParameter("id")
				.passwordParameter("password")
				.defaultSuccessUrl("/admin/dashboard", true)
				.failureUrl("/admin/login?error")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/admin/logout")
				.logoutSuccessUrl("/admin/login?logout")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID")
			)
			.sessionManagement(session -> session
				.sessionFixation(fixation -> fixation.migrateSession())
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false)
			);

		return http.build();
	}

	@Bean
	PasswordEncoder adminPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}