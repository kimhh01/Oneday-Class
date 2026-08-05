package kr.co.oneclass.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {
	private int memberCode;
    private String id;
    private String name;
    private String pass;
    private String phone;
    private String email;
    private int zipcode;
    private String address;
    private String addressDetail;
    private String smsReceiveYN;
    private String emailReceiveYN;

}
