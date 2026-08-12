package kr.co.oneclass.member;

import kr.co.oneclass.member.MemberDAO;
import kr.co.oneclass.member.Member;
import kr.co.oneclass.member.IdFindDTO;
import kr.co.oneclass.member.LoginDTO;
import kr.co.oneclass.member.OAuthLoginDTO;
import kr.co.oneclass.member.PassFindDTO;
import kr.co.oneclass.member.SignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    @Transactional
    public Member processOAuthLogin(OAuthLoginDTO oauthdto) {
        Member member = memberDAO.selectByOAuthId(oauthdto);
        if (member != null) {
            return member;
        }

        boolean isSignedUp = oAuthSignUp(oauthdto);
        if (isSignedUp) {
            return memberDAO.selectMember(oauthdto.getMemberCode());
        }

        return null;
    }

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
        Member member = memberDAO.selectMemberForPassword(passFindDTO);
        if (member == null) {
            return false;
        }

        String tempPassword = createTempPassword();
        String encodedTempPassword = passwordEncoder.encode(tempPassword);

        int result = memberDAO.updateTempPassword(member.getMemberCode(), encodedTempPassword);

        if (result > 0) {
            return emailAuthService.sendTempPassword(passFindDTO.getEmail(), tempPassword);
        }

        return false;
    }

    @Override
    public boolean existsMemberForPassword(PassFindDTO dto) {
        return memberDAO.selectMemberForPassword(dto) != null;
    }

    // ==========================================
    // 신규 구현: 비밀번호 검증 및 회원탈퇴
    // ==========================================

    @Override
    public boolean checkPassword(int memberCode, String rawPassword) {
        Member member = memberDAO.selectMember(memberCode);
        if (member == null || member.getPassword() == null) {
            return false;
        }

        String dbPassword = member.getPassword();

        // BCrypt 암호화 형태 확인 후 일치 여부 비교
        if (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, dbPassword);
        } else {
            return rawPassword.equals(dbPassword);
        }
    }

    @Override
    @Transactional
    public boolean withdrawMember(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return false;
        }

        int code = Integer.parseInt(memberCode);

        // 1) MEMBER_AUTH 관련 데이터 삭제/업데이트
        int result1 = memberDAO.deleteMemberAuth(code);
        
        // 2) MEMBER 테이블 회원 삭제/상태 변경(탈퇴)
        int result2 = memberDAO.deleteMember(code);

        return result1 > 0 || result2 > 0;
    }

    private String createTempPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}