package kr.co.oneclass.member.service;

import kr.co.oneclass.member.domain.Member;
import kr.co.oneclass.member.dto.IdFindDTO;
import kr.co.oneclass.member.dto.LoginDTO;
import kr.co.oneclass.member.dto.OAuthLoginDTO;
import kr.co.oneclass.member.dto.PassFindDTO;
import kr.co.oneclass.member.dto.SignUpDTO;

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
