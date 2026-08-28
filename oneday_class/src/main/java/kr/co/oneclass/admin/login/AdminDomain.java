package kr.co.oneclass.admin.login;

import lombok.Getter;

@Getter
public class AdminDomain {

	private int managerCode;
	private String id, name, email;

	public AdminDomain(int managerCode, String id, String name, String email) {
		this.managerCode = managerCode;
		this.id = id;
		this.name = name;
		this.email = email;
	}

}
