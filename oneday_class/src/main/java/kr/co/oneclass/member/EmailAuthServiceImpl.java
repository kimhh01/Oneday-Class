package kr.co.oneclass.member;

import kr.co.oneclass.member.EmailAuthDAO;
import kr.co.oneclass.member.EmailAuthDomain;
import kr.co.oneclass.member.EmailAuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class EmailAuthServiceImpl implements EmailAuthService {

    @Autowired
    private EmailAuthDAO emailAuthDAO;

    // 메일 발송을 위해 spring-boot-starter-mail 및 SMTP 설정(application.yml)이 필요합니다.
    @Autowired
    private JavaMailSender mailSender;

    private static final long VALID_MINUTES = 5;

    @Override
    public boolean sendCode(String email, String type) {
        String authCode = createAuthCode();

        EmailAuthDTO dto = new EmailAuthDTO();
        dto.setEmail(email);
        dto.setAuthCode(authCode);
        dto.setType(type);
        dto.setIssueDate(new Date());

        int result = emailAuthDAO.insertEmailAuth(dto);
        if (result <= 0) {
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[인증코드] 이메일 인증번호 안내");
            message.setText("인증번호는 [" + authCode + "] 입니다. " + VALID_MINUTES + "분 이내에 입력해주세요.");
            mailSender.send(message);
        } catch (Exception e) {
            // 발송 실패 시 저장된 인증코드는 그대로 두고 실패 응답 (재발송 유도)
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyCode(String email, String authCode) {
        // type을 별도로 넘기지 않는 컨트롤러 시그니처와 맞추기 위해 null로 조회
        // (email 기준 가장 최근 인증코드를 가져옴 - 필요 시 type 파라미터를 추가해 좁힐 수 있습니다)
        EmailAuthDomain saved = emailAuthDAO.selectEmailAuth(email, null);
        if (saved == null || saved.getAuthCode() == null) {
            return false;
        }
        if (!saved.getAuthCode().equals(authCode)) {
            return false;
        }
        long diffMillis = new Date().getTime() - saved.getIssueDate().getTime();
        long diffMinutes = diffMillis / (60 * 1000);
        return diffMinutes <= VALID_MINUTES;
    }
    
 // 3. [추가] 임시 비밀번호 이메일 발송
    @Override
    public boolean sendTempPassword(String email, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[숨쉬당] 임시 비밀번호가 발급되었습니다.");
            message.setText("안녕하세요. 숨쉬당입니다.\n\n" +
                    "회원님의 임시 비밀번호는 [" + tempPassword + "] 입니다.\n" +
                    "로그인 후 마이페이지에서 반드시 비밀번호를 변경해 주세요.");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String createAuthCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리
        return String.valueOf(code);
    }
}
