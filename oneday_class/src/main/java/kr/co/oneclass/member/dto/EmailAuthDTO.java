package kr.co.oneclass.member.dto;

import java.util.Date;

public class EmailAuthDTO {
    private String email;
    private String authCode;
    private String type;
    private Date issueDate;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
}
