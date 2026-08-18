package kr.co.oneclass.admin.login;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminLoginDAO {

	AdminDTO selectAdminById(
            @Param("id") String id
    );
}
