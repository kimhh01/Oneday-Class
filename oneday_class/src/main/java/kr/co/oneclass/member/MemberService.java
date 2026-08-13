package kr.co.oneclass.member;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.member.IdFindDTO;
import kr.co.oneclass.member.LoginDTO;
import kr.co.oneclass.member.OAuthLoginDTO;
import kr.co.oneclass.member.PassFindDTO;
import kr.co.oneclass.member.SignUpDTO;

public interface MemberService {

    Member login(LoginDTO ldto);

    boolean signUp(SignUpDTO signUpDTO);
    
    boolean isIdDuplicate(String id);

    Member oAuthLogin(OAuthLoginDTO oauthdto);

    boolean oAuthSignUp(OAuthLoginDTO oauthdto);
    
    Member processOAuthLogin(OAuthLoginDTO oauthdto);

    String findId(IdFindDTO idFindDTO);

    boolean findPass(PassFindDTO passFindDTO);

    boolean existsMemberForPassword(PassFindDTO dto);

    // ==========================================
    // 신규 추가: 비밀번호 검증 및 회원탈퇴
    // ==========================================

    /**
     * 비밀번호 검증 (회원탈퇴 전 확인용)
     * @param memberCode 회원 코드
     * @param rawPassword 입력한 비밀번호 (평문)
     */
    boolean checkPassword(int memberCode, String rawPassword);

    /**
     * 회원 탈퇴 처리
     * @param memberCode 회원 코드
     */
    boolean withdrawMember(String memberCode);

    // ==========================================
    // 신규 추가: 소셜(구글) 연동 해제용 토큰 폐기
    // ==========================================
    boolean revokeGoogleToken(String accessToken);
    
    // 이메일 중복 확인
	boolean isEmailDuplicate(String email);
}