package kr.co.oneclass.admin.common;

import java.util.Map;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AdminErrorViewResolver implements ErrorViewResolver {

	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {

		Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

		if (status == HttpStatus.NOT_FOUND && requestUri != null && requestUri.toString().startsWith("/admin/")) {

			return new ModelAndView("admin/error/404", model);
		}

		// 관리자 404가 아니면 기존 에러 처리에 맡김
		return null;
	}
}