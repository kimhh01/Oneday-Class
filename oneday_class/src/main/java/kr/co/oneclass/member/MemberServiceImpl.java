package kr.co.oneclass.member;

import kr.co.oneclass.common.AESUtil; // 💡 암복호화 유틸 추가
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
     * 💡 [공통 헬퍼] Member 객체의 민감정보(이름, 이메일, 전화번호) 복호화
     */
    private Member decryptMember(Member member) {
        if (member == null) return null;
        
        // Member 엔티티에 Setter가 구현되어 있어야 합니다.
        if (member.getName() != null) member.setName(AESUtil.decrypt(member.getName()));
        if (member.getEmail() != null) member.setEmail(AESUtil.decrypt(member.getEmail()));
        if (member.getPhone() != null) member.setPhone(AESUtil.decrypt(member.getPhone()));
        
        return member;
    }

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
        } catch (Exception e) { }
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

        memberDAO.insertLoginHistory(member.getMemberCode(), getClientIp());

        // 💡 조회된 회원 객체의 민감정보 복호화 후 반환
        return decryptMember(member);
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
        
        // 비밀번호 단방향 암호화
        signUpDTO.setPass(passwordEncoder.encode(signUpDTO.getPass()));

        // 💡 개인정보(이름, 이메일, 전화번호) 양방향 AES 암호화 후 DB 저장
        signUpDTO.setName(AESUtil.encrypt(signUpDTO.getName()));
        signUpDTO.setEmail(AESUtil.encrypt(signUpDTO.getEmail()));
        signUpDTO.setPhone(AESUtil.encrypt(signUpDTO.getPhone()));

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
        Member member = memberDAO.selectByOAuthId(oauthdto);
        return decryptMember(member); // 💡 복호화 적용
    }

    @Override
    @Transactional
    public boolean oAuthSignUp(OAuthLoginDTO oauthdto) {
        // 💡 소셜 가입 시 민감정보(이름, 이메일) 암호화 후 DB 저장
        OAuthLoginDTO encryptedDto = new OAuthLoginDTO();
        encryptedDto.setMemberCode(oauthdto.getMemberCode());
        encryptedDto.setOauthProviderId(oauthdto.getOauthProviderId()); // 💡 oauthId -> oauthProviderId 변경
        encryptedDto.setProvider(oauthdto.getProvider());
        
        // 이름과 이메일만 암호화 (OAuthLoginDTO에는 phone 필드가 없으므로 제외)
        if (oauthdto.getName() != null) {
            encryptedDto.setName(AESUtil.encrypt(oauthdto.getName()));
        }
        if (oauthdto.getEmail() != null) {
            encryptedDto.setEmail(AESUtil.encrypt(oauthdto.getEmail()));
        }

        int result1 = memberDAO.insertOAuthMember(encryptedDto);       // MEMBER 테이블 INSERT
        int result2 = memberDAO.insertMemberAuthByOAuth(encryptedDto); // MEMBER_AUTH 테이블 INSERT
        
        return result1 > 0 && result2 > 0;
    }
    @Override
    @Transactional
    public Member processOAuthLogin(OAuthLoginDTO oauthdto) {
        Member member = memberDAO.selectByOAuthId(oauthdto);
        if (member != null) {
            memberDAO.insertLoginHistory(member.getMemberCode(), getClientIp());
            return decryptMember(member); // 💡 복호화 적용
        }

        boolean isSignedUp = oAuthSignUp(oauthdto);
        if (isSignedUp) {
            Member newMember = memberDAO.selectMember(oauthdto.getMemberCode());
            if (newMember != null) {
                memberDAO.insertLoginHistory(newMember.getMemberCode(), getClientIp());
            }
            return decryptMember(newMember); // 💡 복호화 적용
        }

        return null;
    }

    @Override
    public String findId(IdFindDTO idFindDTO) {
        if (idFindDTO == null || idFindDTO.getName() == null || idFindDTO.getEmail() == null) {
            return null;
        }

        // 💡 DB 검색을 위해 조건값(이름, 이메일) 암호화
        IdFindDTO encryptedDto = new IdFindDTO();
        encryptedDto.setName(AESUtil.encrypt(idFindDTO.getName()));
        encryptedDto.setEmail(AESUtil.encrypt(idFindDTO.getEmail()));

        return memberDAO.selectId(encryptedDto);
    }

    @Override
    @Transactional
    public boolean findPass(PassFindDTO passFindDTO) {
        // 💡 DB 검색을 위해 암호화된 파라미터 전달
        PassFindDTO encryptedDto = new PassFindDTO();
        encryptedDto.setId(passFindDTO.getId());
        encryptedDto.setName(AESUtil.encrypt(passFindDTO.getName()));
        encryptedDto.setEmail(AESUtil.encrypt(passFindDTO.getEmail()));

        Member member = memberDAO.selectMemberForPassword(encryptedDto);
        if (member == null) {
            return false;
        }

        String tempPassword = createTempPassword();
        String encodedTempPassword = passwordEncoder.encode(tempPassword);

        int result = memberDAO.updateTempPassword(member.getMemberCode(), encodedTempPassword);

        if (result > 0) {
            // 💡 이메일 발송 시에는 평문 이메일(passFindDTO.getEmail())을 전달
            return emailAuthService.sendTempPassword(passFindDTO.getEmail(), tempPassword);
        }

        return false;
    }

    @Override
    public boolean existsMemberForPassword(PassFindDTO dto) {
        PassFindDTO encryptedDto = new PassFindDTO();
        encryptedDto.setId(dto.getId());
        encryptedDto.setName(AESUtil.encrypt(dto.getName()));
        encryptedDto.setEmail(AESUtil.encrypt(dto.getEmail()));

        return memberDAO.selectMemberForPassword(encryptedDto) != null;
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
        int result1 = memberDAO.deleteMemberAuth(code);
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
        // 💡 이메일 중복 확인 시 DB에는 암호화된 이메일이 들어있으므로 암호화하여 조회
        String encryptedEmail = AESUtil.encrypt(email);
        int count = memberDAO.countByLocalEmail(encryptedEmail);
        return count > 0;
    }
}