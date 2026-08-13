package kr.co.oneclass.author.common;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class AuthorSessionDAO {

    private static final String NAMESPACE = "kr.co.oneclass.author.common.AuthorSessionDAO.";

    private final SqlSessionTemplate sqlSession;

    public AuthorSessionDAO(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    public AuthorSessionDTO selectByMemberCode(int memberCode) {
        return sqlSession.selectOne(NAMESPACE + "selectByMemberCode", memberCode);
    }
}
