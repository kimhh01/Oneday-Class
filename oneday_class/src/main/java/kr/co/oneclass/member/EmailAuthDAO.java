package kr.co.oneclass.member;

import kr.co.oneclass.member.EmailAuthDomain;
import kr.co.oneclass.member.EmailAuthDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmailAuthDAO {

    @Insert("INSERT INTO email_auth (email, auth_code, type, issue_date) VALUES (#{email}, #{authCode}, #{type}, SYSDATE)")
    int insertEmailAuth(EmailAuthDTO authDTO);

    // 오라클: ORDER BY 후 ROWNUM = 1을 적용하여 가장 최근 1건만 조회
    @Select("<script>" +
            "SELECT * FROM (" +
            "   SELECT email, auth_code AS authCode, type, issue_date AS issueDate " +
            "   FROM email_auth " +
            "   WHERE email = #{email} " +
            "   <if test='type != null'> AND type = #{type} </if> " +
            "   ORDER BY issue_date DESC" +
            ") WHERE ROWNUM = 1" +
            "</script>")
    @Results({
            @Result(property = "email", column = "email"),
            @Result(property = "authCode", column = "authCode"),
            @Result(property = "type", column = "type"),
            @Result(property = "issueDate", column = "issueDate")
    })
    EmailAuthDomain selectEmailAuth(@Param("email") String email, @Param("type") String type);
}