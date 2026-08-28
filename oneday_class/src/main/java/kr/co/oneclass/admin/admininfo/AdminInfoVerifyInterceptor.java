package kr.co.oneclass.admin.admininfo;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.admin.login.AdminUserDetails;

@Component
public class AdminInfoVerifyInterceptor implements HandlerInterceptor {

	public static final String VERIFIED_MANAGER_CODE = "adminInfoVerifiedManagerCode";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof AdminUserDetails)) {

			response.sendRedirect("/admin/login");

			return false;
		}

		AdminUserDetails loginAdmin = (AdminUserDetails) authentication.getPrincipal();

		int managerCode = loginAdmin.toDomain().getManagerCode();

		HttpSession session = request.getSession(false);

		if (session == null) {

			response.sendRedirect("/admin/info/verify");

			return false;
		}

		Object verifiedCode = session.getAttribute(VERIFIED_MANAGER_CODE);

		if (!(verifiedCode instanceof Integer) || ((Integer) verifiedCode) != managerCode) {

			response.sendRedirect("/admin/info/verify");

			return false;
		}

		return true;
	}
}