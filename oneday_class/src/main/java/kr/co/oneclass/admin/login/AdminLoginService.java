package kr.co.oneclass.admin.login;

public interface AdminLoginService {

    AdminDTO selectAdminForAuthentication(String id);

    AdminDomain selectAdminDomainById(String id);
}
