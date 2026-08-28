package kr.co.oneclass.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmailAuthDAO {

    int insertEmailAuth(EmailAuthDTO authDTO);

    EmailAuthDomain selectEmailAuth(@Param("email") String email, @Param("type") String type);
}