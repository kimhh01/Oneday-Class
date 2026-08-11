package kr.co.oneclass.author.common.service;

import org.springframework.stereotype.Service;

import kr.co.oneclass.author.common.dao.AuthorSessionDAO;
import kr.co.oneclass.author.common.dto.AuthorSessionDTO;

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
