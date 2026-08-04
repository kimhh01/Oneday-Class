package kr.co.oneclass.member;

/**
 * 아이디 찾기 입력 값을 담는 DTO
 */
public class FindIdDTO {

    private String name;
    private String email;
    private String authCode;

    public FindIdDTO() {
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
