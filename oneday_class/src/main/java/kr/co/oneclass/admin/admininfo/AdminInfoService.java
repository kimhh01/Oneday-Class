package kr.co.oneclass.admin.admininfo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminInfoService {

	private final AdminInfoDAO adminInfoDAO;
	private final PasswordEncoder passwordEncoder;

	public AdminInfoService(AdminInfoDAO adminInfoDAO, PasswordEncoder passwordEncoder) {

		this.adminInfoDAO = adminInfoDAO;
		this.passwordEncoder = passwordEncoder;
	}

	public boolean verifyPassword(int managerCode, AdminPasswordVerifyDTO verifyDTO) {

		String encodedPassword = adminInfoDAO.selectAdminPassword(managerCode);

		if (encodedPassword == null) {
			return false;
		}

		return passwordEncoder.matches(verifyDTO.getCurrentPassword(), encodedPassword);
	}

	public AdminInfoDomain getAdminInfo(int managerCode) {

		AdminInfoDTO adminDTO = adminInfoDAO.selectAdminInfo(managerCode);

		return adminDTO == null ? null : toAdminInfoDomain(adminDTO);
	}

	@Transactional
	public boolean updateAdminInfo(int managerCode, AdminInfoUpdateDTO updateDTO) {

		validateBasicInfo(updateDTO);

		boolean passwordChange = updateDTO.getNewPassword() != null && !updateDTO.getNewPassword().isBlank();

		if (!passwordChange) {

			return adminInfoDAO.updateAdminInfo(managerCode, updateDTO.getName(), updateDTO.getEmail()) > 0;
		}

		validatePassword(updateDTO);

		String encodedPassword = passwordEncoder.encode(updateDTO.getNewPassword());

		return adminInfoDAO.updateAdminInfoWithPassword(managerCode, updateDTO.getName(), updateDTO.getEmail(),
				encodedPassword) > 0;
	}

	private void validateBasicInfo(AdminInfoUpdateDTO updateDTO) {

		if (updateDTO.getName() == null || updateDTO.getName().isBlank()) {

			throw new IllegalArgumentException("관리자명을 입력해주세요.");
		}

		if (updateDTO.getEmail() == null || updateDTO.getEmail().isBlank()) {

			throw new IllegalArgumentException("이메일을 입력해주세요.");
		}
	}

	private void validatePassword(AdminInfoUpdateDTO updateDTO) {

		if (updateDTO.getConfirmPassword() == null || updateDTO.getConfirmPassword().isBlank()) {

			throw new IllegalArgumentException("새 비밀번호 확인을 입력해주세요.");
		}

		if (!updateDTO.getNewPassword().equals(updateDTO.getConfirmPassword())) {

			throw new IllegalArgumentException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		}
	}

	private AdminInfoDomain toAdminInfoDomain(AdminInfoDTO adminDTO) {

		return new AdminInfoDomain(adminDTO.getManagerCode(), adminDTO.getId(), adminDTO.getName(),
				adminDTO.getEmail());
	}
}
