package kr.co.oneclass.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDAO {

    // 1. 회원 코드로 회원 기본 정보 및 인증 정보 조회
    Member selectMember(@Param("memberCode") int memberCode);

    // 2. 일반 로그인
    Member selectByLoginId(LoginDTO ldto);

    // 3. OAuth 로그인 (GOOGLE, KAKAO, NAVER 등)
    Member selectByOAuthId(OAuthLoginDTO oauthdto);

    // 4. 일반 회원 기본 정보 저장
    int insertMember(SignUpDTO suDTO);

    // 5. OAuth 전용 회원 기본 정보 저장
    int insertOAuthMember(OAuthLoginDTO oauthdto);

    // 6. 일반 로그인 인증 정보 저장
    int insertMemberAuth(SignUpDTO suDTO);

    // 7. OAuth 로그인 인증 정보 저장
    int insertMemberAuthByOAuth(OAuthLoginDTO oauthdto);

    // 8. 아이디 중복 확인
    int countByLocalLoginId(@Param("id") String id);

    // 9. 아이디 찾기
    String selectId(IdFindDTO iddto);

    // 10. 비밀번호 찾기 전 회원 존재 여부 확인
    Member selectMemberForPassword(PassFindDTO dto);

    // 11. 임시 비밀번호 업데이트
    int updateTempPassword(@Param("memberCode") int memberCode, @Param("password") String password);

    // 12. 회원 탈퇴: member_auth 인증 정보 삭제
    int deleteMemberAuth(@Param("memberCode") int memberCode);

    // 13. 회원 탈퇴: member 기본 정보 삭제 (또는 status='WITHDRAWN' 변경)
    int deleteMember(@Param("memberCode") int memberCode);
}