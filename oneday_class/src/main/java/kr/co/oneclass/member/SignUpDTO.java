package kr.co.oneclass.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {

	  	private String memberId;
	    private String name;
	    private String password;
	    private String passwordConfirm;
	    private String phone;
	    private String email;
	 
	    private String zipcode;
	    private String baseAddress;
	    private String detailAddress;
	 
	    private String agreeTerms;     // [필수] 이용약관 동의 ("Y")
	    private String agreePrivacy;   // [필수] 개인정보 수집 및 이용 동의 ("Y")
	    private String agreeMarketing; // [선택] 마케팅 정보 수신 동의 ("Y" / null)
	    private String agreeSms;       // 마케팅 - SMS 수신 ("Y" / null)
	    private String agreeEmail;     // 마케팅 - 이메일 수신 ("Y" / null)
}
