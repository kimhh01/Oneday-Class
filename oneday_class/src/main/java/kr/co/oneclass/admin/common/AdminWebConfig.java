package kr.co.oneclass.admin.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.oneclass.admin.admininfo.AdminInfoVerifyCleanupInterceptor;
import kr.co.oneclass.admin.admininfo.AdminInfoVerifyInterceptor;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

	private final AdminInfoVerifyInterceptor adminInfoVerifyInterceptor;

	private final AdminInfoVerifyCleanupInterceptor adminInfoVerifyCleanupInterceptor;

	public AdminWebConfig(AdminInfoVerifyInterceptor adminInfoVerifyInterceptor,
			AdminInfoVerifyCleanupInterceptor adminInfoVerifyCleanupInterceptor) {

		this.adminInfoVerifyInterceptor = adminInfoVerifyInterceptor;

		this.adminInfoVerifyCleanupInterceptor = adminInfoVerifyCleanupInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		/*
		 * 관리자 정보 변경 페이지 보호
		 */
		registry.addInterceptor(adminInfoVerifyInterceptor).addPathPatterns("/admin/info/edit", "/admin/info/edit/**");

		/*
		 * 다른 관리자 페이지로 이동하면 비밀번호 재확인 상태 제거
		 */
		registry.addInterceptor(adminInfoVerifyCleanupInterceptor).addPathPatterns("/admin/**").excludePathPatterns(
				"/admin/info/verify", "/admin/info/verify/**", "/admin/info/edit", "/admin/info/edit/**");
	}
}