package kr.co.oneclass.profile;

import kr.co.oneclass.member.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProfileDAO {

    /**
     * 1. 회원 프로필 정보 상세 조회 (회원 기본 정보 + 인증 정보 조인)
     */
    Member selectMember(@Param("memberCode") String memberCode);

    /**
     * 2. 프로필 정보 수정 (이름, 핸드폰 번호, 이메일, 주소, 마케팅 동의)
     */
    int updateProfile(ProfileDTO pdto);

    /**
     * 3. 프로필 이미지 경로 변경
     */
    int updateImg(@Param("memberCode") String memberCode, @Param("img") String img);

    /**
     * 4. 비밀번호 확인을 위한 암호화된 기존 비밀번호 조회
     */
    String selectPasswordByMemberId(@Param("memberCode") String memberCode, @Param("pass") String pass);

    /**
     * 5. 비밀번호 변경
     */
    int updatePass(PassChangeDTO pdto);

    /**
     * 6. 회원 탈퇴 처리 (상태값 변경: ACTIVE -> WITHDRAW)
     */
    int updateStatusToWithdraw(@Param("memberCode") String memberCode);
}