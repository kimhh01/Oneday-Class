package kr.co.oneclass.admin.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMemberSearchDTO {
	private String keyword, status;
	private int page = 1;

}
