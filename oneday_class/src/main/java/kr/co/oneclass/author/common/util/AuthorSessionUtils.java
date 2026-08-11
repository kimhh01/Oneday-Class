package kr.co.oneclass.author.common.util;

import jakarta.servlet.http.HttpSession;

public final class AuthorSessionUtils {

    public static final String AUTHOR_CODE_KEY = "loginAuthorCode";
    public static final String AUTHOR_NAME_KEY = "loginAuthorName";
    public static final String AUTHOR_PROFILE_KEY = "loginAuthorProfileImage";

    private AuthorSessionUtils() {
    }

    public static long getAuthorCode(HttpSession session) {
        Object value = session.getAttribute(AUTHOR_CODE_KEY);
        if (value instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalStateException("로그인한 작가 정보를 찾을 수 없습니다.");
    }

    public static void clear(HttpSession session) {
        session.removeAttribute(AUTHOR_CODE_KEY);
        session.removeAttribute(AUTHOR_NAME_KEY);
        session.removeAttribute(AUTHOR_PROFILE_KEY);
    }
}
