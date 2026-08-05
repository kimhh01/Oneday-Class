package kr.co.oneclass.member.service;

public interface EmailAuthService {

    boolean sendCode(String email, String type);

    boolean verifyCode(String email, String authCode);
    
    boolean sendTempPassword(String email, String tempPassword);
}
