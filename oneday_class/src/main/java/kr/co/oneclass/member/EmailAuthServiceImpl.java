package kr.co.oneclass.member;

import kr.co.oneclass.common.AESUtil; // 💡 AESUtil 임포트 추가
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

    @Autowired
    private JavaMailSender mailSender;

    private static final long VALID_MINUTES = 5;

    @Override
    public boolean sendCode(String email, String type) {
        String authCode = createAuthCode();

        EmailAuthDTO dto = new EmailAuthDTO();
        // 💡 1. DB 테이블 저장 시에는 암호화된 이메일 저장
        dto.setEmail(AESUtil.encrypt(email));
        dto.setAuthCode(authCode);
        dto.setType(type);
        dto.setIssueDate(new Date());

        int result = emailAuthDAO.insertEmailAuth(dto);
        if (result <= 0) {
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // 💡 2. 실제 SMTP 메일 발송 시에는 전달받은 평문 이메일(email) 사용
            message.setTo(email);
            message.setSubject("[인증코드] 이메일 인증번호 안내");
            message.setText("인증번호는 [" + authCode + "] 입니다. " + VALID_MINUTES + "분 이내에 입력해주세요.");
            mailSender.send(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyCode(String email, String authCode) {
        // 💡 3. DB 조회 시에는 암호화된 이메일값으로 SELECT
        String encryptedEmail = AESUtil.encrypt(email);
        EmailAuthDomain saved = emailAuthDAO.selectEmailAuth(encryptedEmail, null);
        
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

    @Override
    public boolean sendTempPassword(String email, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // 💡 4. 실제 SMTP 메일 발송 시에는 평문 이메일 사용
            message.setTo(email);
            message.setSubject("[Oneclass] 임시 비밀번호가 발급되었습니다.");
            message.setText("안녕하세요. Oneclass입니다.\n\n" +
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