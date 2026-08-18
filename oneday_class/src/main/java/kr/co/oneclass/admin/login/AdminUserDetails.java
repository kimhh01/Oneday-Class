package kr.co.oneclass.admin.login;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class AdminUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private int managerCode;
    private String id,password,name,email;

    public AdminUserDetails(AdminDTO aDTO) {
        this.managerCode = aDTO.getManagerCode();
        this.id = aDTO.getId();
        this.password = aDTO.getPassword();
        this.name = aDTO.getName();
        this.email = aDTO.getEmail();
    }

    public AdminDomain toDomain() {
        return new AdminDomain(managerCode,id,name,email);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
