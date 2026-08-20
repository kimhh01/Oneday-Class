package kr.co.oneclass.admin.admininfo;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminInfoVerifyCleanupInterceptor
        implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        // Controller 요청에 대해서만 처리
        // CSS, JS 등 정적 리소스 요청은 제외
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HttpSession session =
                request.getSession(false);

        if (session != null) {

            session.removeAttribute(
                AdminInfoVerifyInterceptor
                    .VERIFIED_MANAGER_CODE
            );
        }

        return true;
    }
}