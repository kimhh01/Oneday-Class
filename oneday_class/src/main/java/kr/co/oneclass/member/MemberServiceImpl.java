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

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

// IP 자동 추출을 위한 Spring RequestContextHolder
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDAO memberDAO;
    
    @Autowired
    private EmailAuthService emailAuthService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 현재 HTTP 요청에서 클라이언트 IP 주소 추출 유틸
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 요청 컨텍스트가 없을 경우 Fallback 처리
        }
        return "127.0.0.1";
    }

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

        // 💡 로그인 성공 시 자동으로 클라이언트 IP를 추출하여 LOGIN_HISTORY 테이블에 기록
        memberDAO.insertLoginHistory(member.getMemberCode(), getClientIp());

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
            // 💡 소셜 로그인 성공 시 접속 이력 기록
            memberDAO.insertLoginHistory(member.getMemberCode(), getClientIp());
            return member;
        }

        boolean isSignedUp = oAuthSignUp(oauthdto);
        if (isSignedUp) {
            Member newMember = memberDAO.selectMember(oauthdto.getMemberCode());
            if (newMember != null) {
                // 💡 신규 소셜 가입 후 로그인 시 접속 이력 기록
                memberDAO.insertLoginHistory(newMember.getMemberCode(), getClientIp());
            }
            return newMember;
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

    @Override
    public boolean checkPassword(int memberCode, String rawPassword) {
        Member member = memberDAO.selectMember(memberCode);
        if (member == null || member.getPassword() == null) {
            return false;
        }

        String dbPassword = member.getPassword();

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

        // 1) MEMBER_AUTH 관련 데이터 완전히 삭제
        int result1 = memberDAO.deleteMemberAuth(code);
        
        // 2) MEMBER 테이블 회원 개인정보 마스킹 및 상태 변경(탈퇴)
        int result2 = memberDAO.deleteMember(code);

        return result1 > 0 || result2 > 0;
    }

    @Override
    public boolean revokeGoogleToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return false;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + accessToken;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, entity, String.class);

            return response.getStatusCode().is2xxSuccessful();
            
        } catch (Exception e) {
            System.err.println("구글 토큰 폐기 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    private String createTempPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

	@Override
	public boolean isEmailDuplicate(String email) {
        int count = memberDAO.countByLocalEmail(email);
        return count > 0;
	}
}