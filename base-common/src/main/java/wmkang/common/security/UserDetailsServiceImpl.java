package wmkang.common.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import wmkang.domain.annotation.ManageReadTransactional;
import wmkang.domain.manage.repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Lazy
    @Autowired
    private UserRepository userRepo;


    @Override
    @ManageReadTransactional
    public UserDetails loadUserByUsername(final String username) {
        return userRepo.findByEmailAndActive(username, true).map(UserDetails::new)
                        .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
