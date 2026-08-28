package kr.co.oneclass.admin.member;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AdminMemberDomain {

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

}