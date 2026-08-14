package kr.co.oneclass.author.common;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.member.Member;

@Component
public class AuthorAccessInterceptor implements HandlerInterceptor {

    private final AuthorSessionService authorSessionService;

    public AuthorAccessInterceptor(AuthorSessionService authorSessionService) {
        this.authorSessionService = authorSessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Member loginMember = session == null ? null : (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            redirect(response, request.getContextPath() + "/member/login/general");
            return false;
        }

        AuthorSessionDTO author = authorSessionService.getAuthorByMemberCode(loginMember.getMemberCode());
        if (author == null) {
            AuthorSessionUtils.clear(session);
            redirectAccess(response, request, "not-author");
            return false;
        }
        if (!"승인".equals(author.getApprovalStatus())) {
            AuthorSessionUtils.clear(session);
            redirectAccess(response, request, "pending");
            return false;
        }

        session.setAttribute(AuthorSessionUtils.AUTHOR_CODE_KEY, author.getAuthorCode());
        session.setAttribute(AuthorSessionUtils.AUTHOR_NAME_KEY, author.getActivityName());
        session.setAttribute(AuthorSessionUtils.AUTHOR_PROFILE_KEY, author.getProfileImagePath());
        return true;
    }

    private void redirectAccess(HttpServletResponse response, HttpServletRequest request, String reason)
            throws IOException {
        String encoded = URLEncoder.encode(reason, StandardCharsets.UTF_8);
        redirect(response, request.getContextPath() + "/author/access?reason=" + encoded);
    }

    private void redirect(HttpServletResponse response, String location) throws IOException {
        response.sendRedirect(location);
    }
}
