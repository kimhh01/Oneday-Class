package kr.co.oneclass.member.dto;

public class LoginDTO {
    private String id;       // 로그인 아이디 (본 프로젝트에서는 email을 아이디로 사용)
    private String password; // 평문 비밀번호 (서비스 계층에서 BCrypt 비교)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
