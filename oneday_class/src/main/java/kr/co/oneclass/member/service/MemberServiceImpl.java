package kr.co.oneclass.member.service;

import kr.co.oneclass.member.dao.MemberDAO;
import kr.co.oneclass.member.domain.Member;
import kr.co.oneclass.member.dto.IdFindDTO;
import kr.co.oneclass.member.dto.LoginDTO;
import kr.co.oneclass.member.dto.OAuthLoginDTO;
import kr.co.oneclass.member.dto.PassFindDTO;
import kr.co.oneclass.member.dto.SignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDAO memberDAO;
    
    @Autowired
    private EmailAuthService emailAuthService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Member login(LoginDTO ldto) {
        Member member = memberDAO.selectByLoginId(ldto);
        if (member == null) {
            return null;
        }

        String rawPassword = ldto.getPassword();
        String dbPassword = member.getPassword();

        if (dbPassword == null) {
            return null;
        }

        boolean isMatch = false;

        if (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$")) {
            isMatch = passwordEncoder.matches(rawPassword, dbPassword);
        } else {
            isMatch = rawPassword.equals(dbPassword);
        }

        if (!isMatch) {
            return null;
        }

        return member;
    }

    @Override
    @Transactional
    public boolean signUp(SignUpDTO signUpDTO) {
        if (signUpDTO.getSmsReceiveYN() == null) {
            signUpDTO.setSmsReceiveYN("N");
        }
        if (signUpDTO.getEmailReceiveYN() == null) {
            signUpDTO.setEmailReceiveYN("N");
        }

        signUpDTO.setPass(passwordEncoder.encode(signUpDTO.getPass()));

        int result1 = memberDAO.insertMember(signUpDTO);
        int result2 = memberDAO.insertMemberAuth(signUpDTO);

        return result1 > 0 && result2 > 0;
    }
    
    @Override
    public boolean isIdDuplicate(String id) {
        int count = memberDAO.countByLocalLoginId(id);
        return count > 0;
    }

    @Override
    public Member oAuthLogin(OAuthLoginDTO oauthdto) {
        return memberDAO.selectByOAuthId(oauthdto);
    }

    @Override
    @Transactional
    public boolean oAuthSignUp(OAuthLoginDTO oauthdto) {
        int result1 = memberDAO.insertOAuthMember(oauthdto);       // MEMBER 테이블 생성
        int result2 = memberDAO.insertMemberAuthByOAuth(oauthdto); // MEMBER_AUTH 테이블 생성
        return result1 > 0 && result2 > 0;
    }

    // OAuth 로그인 및 최초 로그인 시 자동 회원가입 통합 처리
    @Override
    @Transactional
    public Member processOAuthLogin(OAuthLoginDTO oauthdto) {
        // 1. 기존 OAuth 가입 여부 확인
        Member member = memberDAO.selectByOAuthId(oauthdto);
        if (member != null) {
            return member; // 이미 가입된 회원이면 객체 반환
        }

        // 2. 신규 회원이면 회원가입 진행
        boolean isSignedUp = oAuthSignUp(oauthdto);
        if (isSignedUp) {
            // 새로 생성된 memberCode로 회원 정보 재조회
            return memberDAO.selectMember(oauthdto.getMemberCode());
        }

        return null;
    }

    // [아이디 찾기] Null 안전검사 추가
    @Override
    public String findId(IdFindDTO idFindDTO) {
        if (idFindDTO == null || idFindDTO.getName() == null || idFindDTO.getEmail() == null) {
            return null;
        }
        return memberDAO.selectId(idFindDTO);
    }

    @Override
    @Transactional
    public boolean findPass(PassFindDTO passFindDTO) {
        // 1. 회원 존재 여부 확인
        Member member = memberDAO.selectMemberForPassword(passFindDTO);
        if (member == null) {
            return false;
        }

        // 2. 10자리 임시 비밀번호 생성 및 BCrypt 암호화
        String tempPassword = createTempPassword();
        String encodedTempPassword = passwordEncoder.encode(tempPassword);

        // 3. MEMBER_AUTH 테이블 비밀번호 업데이트
        int result = memberDAO.updateTempPassword(member.getMemberCode(), encodedTempPassword);

        // 4. DB 수정 성공 시 EmailAuthService를 통해 임시 비밀번호 이메일 전송
        if (result > 0) {
            return emailAuthService.sendTempPassword(passFindDTO.getEmail(), tempPassword);
        }

        return false;
    }

    @Override
    public boolean existsMemberForPassword(PassFindDTO dto) {
        return memberDAO.selectMemberForPassword(dto) != null;
    }

    private String createTempPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}