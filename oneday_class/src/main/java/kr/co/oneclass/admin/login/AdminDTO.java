package kr.co.oneclass.admin.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AdminDTO {

	private int managerCode;
	private String id, password, name, email;

	public AdminDomain toDomain() {
		return new AdminDomain(managerCode,id,name,email);
	}
}
