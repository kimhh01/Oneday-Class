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
    
    /**
     * 💡 추가: 작가 정보 조회 후 없으면 '승인' 상태로 자동 생성(INSERT)
     */
    @Transactional
    public AuthorSessionDTO getOrCreateAuthor(int memberCode) {
        AuthorSessionDTO author = authorSessionDAO.selectByMemberCode(memberCode);

        // 작가 정보가 없는 경우 기본 데이터 등록
        if (author == null) {
            authorSessionDAO.insertInitialAuthor(memberCode);
            author = authorSessionDAO.selectByMemberCode(memberCode);
        }

        return author;
    }
}
