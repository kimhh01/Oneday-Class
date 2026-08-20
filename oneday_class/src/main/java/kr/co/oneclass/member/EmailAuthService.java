package kr.co.oneclass.member;

public interface EmailAuthService {

    boolean sendCode(String email, String type);

    boolean verifyCode(String email, String authCode);
    
    boolean sendTempPassword(String email, String tempPassword);
}
