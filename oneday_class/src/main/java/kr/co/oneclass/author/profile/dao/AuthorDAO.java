package kr.co.oneclass.author.profile.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import kr.co.oneclass.author.profile.dto.AuthorProfileDTO;

@Repository
public class AuthorDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.profile.dao.AuthorDAO.";

    private final SqlSessionTemplate sqlSession;

    public AuthorDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 작가 프로필 정보를 조회한다 (CREATOR + MEMBER 조인)
    public AuthorProfileDTO selectAuthorProfile(long authorCode) {
        return sqlSession.selectOne(NAMESPACE + "selectAuthorProfile", authorCode);
    }

    // 작가 프로필 정보를 수정한다 (CREATOR 만 변경한다)
    public int updateAuthorProfile(AuthorProfileDTO apDTO) {
        return sqlSession.update(NAMESPACE + "updateAuthorProfile", apDTO);
    }
}
