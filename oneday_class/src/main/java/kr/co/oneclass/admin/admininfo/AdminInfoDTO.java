package kr.co.oneclass.admin.admininfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminInfoDTO {

	private int managerCode;

	private String id;
	private String password;

	private String name;
	private String email;
}
