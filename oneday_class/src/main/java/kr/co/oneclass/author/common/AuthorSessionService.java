package kr.co.oneclass.author.common;

import org.springframework.stereotype.Service;


@Service
public class AuthorSessionService {

    private final AuthorSessionDAO authorSessionDAO;

    public AuthorSessionService(AuthorSessionDAO authorSessionDAO) {
        this.authorSessionDAO = authorSessionDAO;
    }

    public AuthorSessionDTO getAuthorByMemberCode(int memberCode) {
        return authorSessionDAO.selectByMemberCode(memberCode);
    }
}
