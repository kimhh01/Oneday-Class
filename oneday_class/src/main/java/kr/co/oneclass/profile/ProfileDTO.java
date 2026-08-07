package kr.co.oneclass.profile;

public class ProfileDTO {
    private int memberCode;
    private String name;
    private String phone;
    private String email;

    public ProfileDTO() {}

    public ProfileDTO(int memberCode, String name, String phone, String email) {
        this.memberCode = memberCode;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getMemberCode() { return memberCode; }
    public void setMemberCode(int memberCode) { this.memberCode = memberCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}