package kr.co.oneclass.author.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthorSessionService {

	@Autowired
    private final AuthorSessionDAO authorSessionDAO;

    public AuthorSessionService(AuthorSessionDAO authorSessionDAO) {
        this.authorSessionDAO = authorSessionDAO;
    }

    public AuthorSessionDTO getAuthorByMemberCode(int memberCode) {
        return authorSessionDAO.selectByMemberCode(memberCode);
    }

    @Transactional
    public void activateCompletedAuthor(int memberCode) {
        authorSessionDAO.activateCompletedAuthor(memberCode);
    }
}
