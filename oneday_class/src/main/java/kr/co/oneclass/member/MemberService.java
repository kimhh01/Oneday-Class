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
}
