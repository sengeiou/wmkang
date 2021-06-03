package wmkang.common.config;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import wmkang.common.security.UserDetails;
import wmkang.domain.enums.Role;
import wmkang.domain.enums.Shard;
import wmkang.domain.manage.entity.User;

/**
 * 단위 테스트 환경 설정 클래스
 */
@TestConfiguration
public class TestConfig {


    /**
     * 단위 테스트용 UserDetailsService
     */
    @Primary
    @Service
    class TestUserDetailsImpl implements UserDetailsService {

        Map<String, UserDetails> userMap = new HashMap<>();

        @PostConstruct
        public void init(){
            userMap = Stream.of(
                    new User(1, "passwd", "사용자1", "user1@wmkang.com",   Role.USER,    Shard.FIRST, true),
                    new User(2, "passwd", "사용자2", "user2@wmkang.com",   Role.USER,    Shard.FIRST, true),
                    new User(3, "passwd", "사용자3", "user3@wmkang.com",   Role.USER,    Shard.FIRST, true),
                    new User(4, "passwd", "관리자",  "manager@wmkang.com", Role.MANAGER, Shard.FIRST, true),
                    new User(5, "passwd", "어드민",  "admin@wmkang.com",   Role.ADMIN,   Shard.FIRST, true)
            ).map(UserDetails::new).collect(Collectors.toMap(UserDetails::getEmail, Function.identity()));
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            UserDetails user = userMap.get(username);
            if(user == null)
                throw new UsernameNotFoundException(username);
            return user;
        }
    }
}