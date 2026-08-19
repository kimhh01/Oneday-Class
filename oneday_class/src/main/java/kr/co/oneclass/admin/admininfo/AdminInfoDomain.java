package kr.co.oneclass.admin.admininfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminInfoDomain {

	private int managerCode;

	private String id;
	private String name;
	private String email;
}
