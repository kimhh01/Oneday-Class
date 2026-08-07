package kr.co.oneclass.member;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.member.*;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MemberDAO {

    @Select("SELECT m.*, a.login_type, a.local_login_id, a.oauth_provider_id " +
            "FROM member m " +
            "JOIN member_auth a ON m.member_code = a.member_code " +
            "WHERE m.member_code = #{memberCode}")
    @Results(id = "memberResultMap", value = {
            @Result(property = "memberCode", column = "member_code", id = true),
            @Result(property = "email", column = "email"),
            @Result(property = "password", column = "local_password"),
            @Result(property = "name", column = "name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "zipCode", column = "zip_code"),
            @Result(property = "address", column = "address"),
            @Result(property = "address2", column = "address2"),
            @Result(property = "loginType", column = "login_type"),
            @Result(property = "oauthProviderId", column = "oauth_provider_id"),
            @Result(property = "status", column = "status"),
            @Result(property = "profileImg", column = "profile_img"),
            @Result(property = "inputDate", column = "input_date"),
            @Result(property = "smsReceiveYN", column = "sms_receive_yn"),
            @Result(property = "emailReceiveYN", column = "email_receive_yn")
    })
    Member selectMember(@Param("memberCode") int memberCode);

    // 일반 로그인
    @Select("SELECT m.*, a.login_type, a.local_login_id, a.local_password, a.oauth_provider_id " +
            "FROM member m " +
            "JOIN member_auth a ON m.member_code = a.member_code " +
            "WHERE a.local_login_id = #{id} AND a.login_type = 'LOCAL'")
    @ResultMap("memberResultMap")
    Member selectByLoginId(LoginDTO ldto);

    // OAuth 로그인 (GOOGLE, KAKAO, NAVER 등)
    @Select("SELECT m.*, a.login_type, a.oauth_provider_id " +
            "FROM member m " +
            "JOIN member_auth a ON m.member_code = a.member_code " +
            "WHERE a.oauth_provider_id = #{oauthProviderId} AND a.login_type = #{provider}")
    @ResultMap("memberResultMap")
    Member selectByOAuthId(OAuthLoginDTO oauthdto);

    // 1. 일반 회원 기본 정보 저장
    @Insert("INSERT INTO member " +
            "(member_code, email, name, phone, zipcode, address, address2, sms_receive, email_receive, status, input_date, reservation_num) " +
            "VALUES " +
            "(#{memberCode}, #{email}, #{name}, #{phone}, #{zipcode}, #{address}, #{addressDetail}, " +
            "#{smsReceiveYN, jdbcType=VARCHAR}, #{emailReceiveYN, jdbcType=VARCHAR}, 'ACTIVE', SYSDATE, 0)")
    @SelectKey(statement = "SELECT seq_member_code.NEXTVAL FROM dual", keyProperty = "memberCode", before = true, resultType = int.class)
    int insertMember(SignUpDTO suDTO);

    // 2. OAuth 전용 회원 기본 정보 저장 (memberCode 시퀀스 발급)
    @Insert("INSERT INTO member " +
            "(member_code, email, name, status, input_date, reservation_num, sms_receive, email_receive) " +
            "VALUES " +
            "(#{memberCode}, #{email}, #{name}, 'ACTIVE', SYSDATE, 0, 'N', 'N')")
    @SelectKey(statement = "SELECT seq_member_code.NEXTVAL FROM dual", keyProperty = "memberCode", before = true, resultType = int.class)
    int insertOAuthMember(OAuthLoginDTO oauthdto);

    // 3. 일반 로그인 인증 정보 저장
    @Insert("INSERT INTO member_auth " +
            "(member_auth_code, member_code, login_type, local_login_id, local_password) " +
            "VALUES " +
            "(seq_member_auth_code.NEXTVAL, #{memberCode}, 'LOCAL', #{id}, #{pass})")
    int insertMemberAuth(SignUpDTO suDTO);
    
    // 4. OAuth 로그인 인증 정보 저장 (seq_member_auth_code 적용)
    @Insert("INSERT INTO member_auth (member_auth_code, member_code, login_type, oauth_provider_id) " +
            "VALUES (seq_member_auth_code.NEXTVAL, #{memberCode}, #{provider}, #{oauthProviderId})")
    int insertMemberAuthByOAuth(OAuthLoginDTO oauthdto);

    @Select("SELECT COUNT(*) FROM member_auth WHERE local_login_id = #{id} AND login_type = 'LOCAL'")
    int countByLocalLoginId(@Param("id") String id);

    @Select("SELECT a.local_login_id FROM member m " +
            "JOIN member_auth a ON m.member_code = a.member_code " +
            "WHERE m.name = #{name} AND m.email = #{email} AND a.login_type = 'LOCAL' AND ROWNUM = 1")
    String selectId(IdFindDTO iddto);

    @Select("SELECT m.*, a.local_login_id " +
            "FROM member m " +
            "JOIN member_auth a ON m.member_code = a.member_code " +
            "WHERE a.local_login_id = #{id} AND m.name = #{name} AND m.email = #{email} AND a.login_type = 'LOCAL'")
    @ResultMap("memberResultMap")
    Member selectMemberForPassword(PassFindDTO dto);

    @Update("UPDATE member_auth SET local_password = #{password} " +
            "WHERE member_code = #{memberCode} AND login_type = 'LOCAL'")
    int updateTempPassword(@Param("memberCode") int memberCode, @Param("password") String password);
}