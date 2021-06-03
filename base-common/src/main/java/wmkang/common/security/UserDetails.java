package wmkang.common.security;


import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.ToString;
import wmkang.domain.enums.Role;
import wmkang.domain.enums.Shard;
import wmkang.domain.manage.entity.User;


@SuppressWarnings("serial")
@ToString
@Getter
public class UserDetails extends org.springframework.security.core.userdetails.User {


    private Integer id;
    private String  name;
    private String  email;
    private Role    role;
    private Shard   shard;


    public UserDetails(String username) {
        super(username, null, null);
    }

    public UserDetails(User user) {
        super(  user.getEmail(),
                user.getPasswd(),
                user.isActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
                );
        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        role = user.getRole();
        shard = user.getShard();
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }
}
