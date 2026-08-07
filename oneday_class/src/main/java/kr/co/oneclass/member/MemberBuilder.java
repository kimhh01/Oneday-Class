package kr.co.oneclass.member;

import java.util.Date;

public class MemberBuilder {

    int memberCode;
    String phone;
    String email;
    int zipCode;
    String address;
    String address2;
    String name;
    String status;
    String profileImg;
    Date inputDate;
    String loginType;
    String password;
    String smsReceiveYN;
    String emailReceiveYN;
    String oauthProviderId;

    public MemberBuilder memberCode(int memberCode) { this.memberCode = memberCode; return this; }
    public MemberBuilder phone(String phone) { this.phone = phone; return this; }
    public MemberBuilder email(String email) { this.email = email; return this; }
    public MemberBuilder zipCode(int zipCode) { this.zipCode = zipCode; return this; }
    public MemberBuilder address(String address) { this.address = address; return this; }
    public MemberBuilder address2(String address2) { this.address2 = address2; return this; }
    public MemberBuilder name(String name) { this.name = name; return this; }
    public MemberBuilder status(String status) { this.status = status; return this; }
    public MemberBuilder profileImg(String profileImg) { this.profileImg = profileImg; return this; }
    public MemberBuilder inputDate(Date inputDate) { this.inputDate = inputDate; return this; }
    public MemberBuilder loginType(String loginType) { this.loginType = loginType; return this; }
    public MemberBuilder password(String password) { this.password = password; return this; }
    public MemberBuilder smsReceiveYN(String smsReceiveYN) { this.smsReceiveYN = smsReceiveYN; return this; }
    public MemberBuilder emailReceiveYN(String emailReceiveYN) { this.emailReceiveYN = emailReceiveYN; return this; }
    public MemberBuilder oauthProviderId(String oauthProviderId) { this.oauthProviderId = oauthProviderId; return this; }

    public Member build() {
        return new Member(this);
    }
}
