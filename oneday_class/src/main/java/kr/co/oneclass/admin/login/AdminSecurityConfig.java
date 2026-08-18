package kr.co.oneclass.admin.login;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AdminSecurityConfig {

	@Bean
	SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/admin/login", "/css/**", "/js/**", "/images/**")
				.permitAll()
				.requestMatchers("/admin/**")
				.hasRole("ADMIN")
				.anyRequest()
				.permitAll())
				.formLogin(form -> form
				.loginPage("/admin/login")
				.loginProcessingUrl("/admin/login")
				.usernameParameter("id")
				.passwordParameter("password")
				.defaultSuccessUrl("/admin/dashboard", true)
				.failureUrl("/admin/login?error")
				.permitAll())
				.logout(logout -> logout
				.logoutUrl("/admin/logout")
				.logoutSuccessUrl("/admin/login?logout")
				.invalidateHttpSession(true)
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID"))
				.sessionManagement(session -> session
				.sessionFixation(fixation -> fixation.migrateSession())
				.maximumSessions(1)
				.maxSessionsPreventsLogin(false));

		return http.build();
	}

	@Bean
	PasswordEncoder adminPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
