package kr.co.oneclass.admin.admininfo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminInfoDAO {

	AdminInfoDTO selectAdminInfo(@Param("managerCode") int managerCode);

	String selectAdminPassword(@Param("managerCode") int managerCode);

	int updateAdminInfo(@Param("managerCode") int managerCode, @Param("name") String name,
			@Param("email") String email);

	int updateAdminInfoWithPassword(@Param("managerCode") int managerCode, @Param("name") String name,
			@Param("email") String email, @Param("encodedPassword") String encodedPassword);
}
