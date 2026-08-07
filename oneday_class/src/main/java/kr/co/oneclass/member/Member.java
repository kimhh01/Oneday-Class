package kr.co.oneclass.member;

import java.util.Date;

/**
 * 회원 도메인 객체.
 * - 일반적으로는 Member.builder()...build() 로 생성합니다.
 * - MyBatis 결과 매핑을 위해 no-arg 생성자 + setter도 함께 제공합니다.
 *   (원본 다이어그램은 완전한 불변 객체였지만, MyBatis가 setter 없이는
 *    조회 결과를 채울 수 없어서 실무적으로 완화했습니다.)
 */
public class Member {

    private int memberCode;
    private String phone;
    private String email;
    private int zipCode;
    private String address;
    private String address2;
    private String name;
    private String status;
    private String profileImg;
    private Date inputDate;
    private String loginType;       // GENERAL, google 등
    private String password;
    private String smsReceiveYN;
    private String emailReceiveYN;
    private String oauthProviderId; // 구글 등 OAuth 제공자의 고유 ID (OAuth 로그인 조회를 위해 추가)

    public Member() {
        // MyBatis 등 프레임워크용 기본 생성자
    }

    Member(MemberBuilder builder) {
        this.memberCode = builder.memberCode;
        this.phone = builder.phone;
        this.email = builder.email;
        this.zipCode = builder.zipCode;
        this.address = builder.address;
        this.address2 = builder.address2;
        this.name = builder.name;
        this.status = builder.status;
        this.profileImg = builder.profileImg;
        this.inputDate = builder.inputDate;
        this.loginType = builder.loginType;
        this.password = builder.password;
        this.smsReceiveYN = builder.smsReceiveYN;
        this.emailReceiveYN = builder.emailReceiveYN;
        this.oauthProviderId = builder.oauthProviderId;
    }

    public static MemberBuilder builder() {
        return new MemberBuilder();
    }

    public int getMemberCode() { return memberCode; }
    public void setMemberCode(int memberCode) { this.memberCode = memberCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getZipCode() { return zipCode; }
    public void setZipCode(int zipCode) { this.zipCode = zipCode; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProfileImg() { return profileImg; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }

    public Date getInputDate() { return inputDate; }
    public void setInputDate(Date inputDate) { this.inputDate = inputDate; }

    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSmsReceiveYN() { return smsReceiveYN; }
    public void setSmsReceiveYN(String smsReceiveYN) { this.smsReceiveYN = smsReceiveYN; }

    public String getEmailReceiveYN() { return emailReceiveYN; }
    public void setEmailReceiveYN(String emailReceiveYN) { this.emailReceiveYN = emailReceiveYN; }

    public String getOauthProviderId() { return oauthProviderId; }
    public void setOauthProviderId(String oauthProviderId) { this.oauthProviderId = oauthProviderId; }

    @Override
    public String toString() {
        return "Member{" +
                "memberCode=" + memberCode +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
