package kr.co.oneclass.admin.member;

import java.util.Date;

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
public class AdminMemberDTO {

	private int memberCode;
	private int reservationNum;
	private int totalPayment;
	private int cancelCount;

	private String phone;
	private String email;
	private String zipcode;
	private String address;
	private String address2;
	private String name;
	private String status;
	private String smsReceive;
	private String emailReceive;
	private String image;

	private Date humanDate;
	private Date inputDate;
	private Date lastLoginDate;

	public AdminMemberDomain toDomain() {

		return new AdminMemberDomain(memberCode, reservationNum, totalPayment, cancelCount, phone, email, zipcode,
				address, address2, name, status, smsReceive, emailReceive, image, humanDate, inputDate, lastLoginDate);
	}

}