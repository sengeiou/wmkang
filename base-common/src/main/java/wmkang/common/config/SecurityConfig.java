package wmkang.common.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import wmkang.common.security.AuthenticationEntryPoint;
import wmkang.common.security.UserDetailsServiceImpl;
import wmkang.domain.enums.Role;

/**
 * Spring Seccurity 설정
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint;


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .headers().frameOptions().disable()                             // for H2 Console
            .and()
            .authorizeRequests()
            .antMatchers("/open/**").permitAll()
            .antMatchers("/h2/**").permitAll()                              // for H2 console
            .antMatchers("/file/download").permitAll()                      // for Test
            .antMatchers("/test/role/user/**").hasAuthority("USER")         // for Test
            .antMatchers("/test/role/manager/**").hasAuthority("MANAGER")   // for Test
            .antMatchers("/test/role/admin/**").hasAuthority("ADMIN")       // for Test
            .antMatchers("/test/**").permitAll()                            // for Test
            .anyRequest().authenticated()
        .and()
            .httpBasic()
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
            .csrf().disable()
            .formLogin().disable();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(-1);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return Role.buildRoleHierachy();
    }


    @EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
    public static class SecurityMethodConfig extends GlobalMethodSecurityConfiguration {

        @Bean
        GrantedAuthorityDefaults grantedAuthorityDefaults() {
            return new GrantedAuthorityDefaults("");
        }
    }
}
