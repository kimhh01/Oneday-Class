package kr.co.oneclass.admin.admininfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInfoUpdateDTO {

	private String name;
	private String email;

	private String newPassword;
	private String confirmPassword;
}
