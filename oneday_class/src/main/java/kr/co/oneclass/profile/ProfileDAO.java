package kr.co.oneclass.profile;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.PassChangeDTO;
import kr.co.oneclass.profile.ProfileDTO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProfileDAO {

	/**
     * 1. 회원 프로필 정보 상세 조회 (회원 기본 정보 + 인증 정보 조인)
     */
    @Select("SELECT m.member_code AS memberCode, " +
            "       m.email, " +
            "       m.name, " +
            "       m.phone, " +
            "       m.zipcode AS zipCode, " +
            "       m.address, " +
            "       m.address2, " +
            "       m.status, " +
            "       m.image AS profileImg, " +
            "       m.input_date AS inputDate, " +
            "       a.login_type AS loginType, " +
            "       a.local_login_id AS localLoginId, " +
            "       a.oauth_provider_id AS oauthProviderId " +
            "FROM member m " +
            "LEFT JOIN member_auth a ON m.member_code = a.member_code " +
            "WHERE m.member_code = TO_NUMBER(#{memberCode})")
    Member selectMember(@Param("memberCode") String memberCode);

    /**
     * 2. 프로필 정보 수정 (이름, 핸드폰 번호, 이메일)
     */
    @Update("UPDATE member " +
            "SET name = #{name}, phone = #{phone}, email = #{email} " +
            "WHERE member_code = #{memberCode}")
    int updateProfile(ProfileDTO pdto);

    /**
     * 3. 프로필 이미지 경로 변경
     */
    @Update("UPDATE member " +
            "SET image = #{img} " +
            "WHERE member_code = TO_NUMBER(#{memberCode})")
    int updateImg(@Param("memberCode") String memberCode, @Param("img") String img);

    /**
     * 4. 비밀번호 확인을 위한 암호화된 기존 비밀번호 조회
     */
    @Select("SELECT a.local_password " +
            "FROM member_auth a " +
            "WHERE a.member_code = TO_NUMBER(#{memberCode}) AND a.login_type = 'LOCAL'")
    String selectPasswordByMemberId(@Param("memberCode") String memberCode, @Param("pass") String pass);

    /**
     * 5. 비밀번호 변경
     */
    @Update("UPDATE member_auth " +
            "SET local_password = #{newPass} " +
            "WHERE member_code = #{memberCode} AND login_type = 'LOCAL'")
    int updatePass(PassChangeDTO pdto);

    /**
     * 6. 회원 탈퇴 처리 (상태값 변경: ACTIVE -> WITHDRAW)
     */
    @Update("UPDATE member " +
            "SET status = 'WITHDRAW' " +
            "WHERE member_code = TO_NUMBER(#{memberCode})")
    int updateStatusToWithdraw(@Param("memberCode") String memberCode);
}