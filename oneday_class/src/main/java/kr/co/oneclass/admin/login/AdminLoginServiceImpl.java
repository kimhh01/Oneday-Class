package kr.co.oneclass.admin.login;

import org.springframework.stereotype.Service;

@Service
public class AdminLoginServiceImpl implements AdminLoginService {

	private final AdminLoginDAO adminLoginDAO;

	public AdminLoginServiceImpl(AdminLoginDAO adminLoginDAO) {
		this.adminLoginDAO = adminLoginDAO;
	}

	@Override
	public AdminDTO selectAdminForAuthentication(String id) {
		return adminLoginDAO.selectAdminById(id);
	}

	@Override
	public AdminDomain selectAdminDomainById(String id) {

		AdminDTO adminDTO = adminLoginDAO.selectAdminById(id);

		if (adminDTO == null) {
			return null;
		}

		return adminDTO.toDomain();
	}
}
