package kr.co.oneclass.profile;

public class PassChangeDTO {
    private int memberCode;
    private String currentPass;
    private String newPass;
    private String newPassConfirm;

    public PassChangeDTO() {}

    public PassChangeDTO(int memberCode, String currentPass, String newPass, String newPassConfirm) {
        this.memberCode = memberCode;
        this.currentPass = currentPass;
        this.newPass = newPass;
        this.newPassConfirm = newPassConfirm;
    }

    public int getMemberCode() { return memberCode; }
    public void setMemberCode(int memberCode) { this.memberCode = memberCode; }

    public String getCurrentPass() { return currentPass; }
    public void setCurrentPass(String currentPass) { this.currentPass = currentPass; }

    public String getNewPass() { return newPass; }
    public void setNewPass(String newPass) { this.newPass = newPass; }

    public String getNewPassConfirm() { return newPassConfirm; }
    public void setNewPassConfirm(String newPassConfirm) { this.newPassConfirm = newPassConfirm; }
}