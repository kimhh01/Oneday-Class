package kr.co.oneclass.member;

/**
 * 비밀번호 찾기(본인확인) 입력 값을 담는 DTO
 */
public class FindPasswordDTO {

    private String memberId;
    private String name;
    private String email;
    private String authCode;

    public FindPasswordDTO() {
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
