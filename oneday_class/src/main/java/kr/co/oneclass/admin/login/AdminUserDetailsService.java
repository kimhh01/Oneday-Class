package kr.co.oneclass.admin.login;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

	private AdminLoginService als;

	public AdminUserDetailsService(AdminLoginService als) {

		this.als = als;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AdminDTO adminDTO = als.selectAdminForAuthentication(username);

		if (adminDTO == null) {
			throw new UsernameNotFoundException("관리자 계정을 찾을 수 없습니다.");
		}

		return new AdminUserDetails(adminDTO);
	}
}
