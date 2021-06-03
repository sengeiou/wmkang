package wmkang.task.controller;


import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.security.UserInfoDto;
import wmkang.common.util.Util;
import wmkang.domain.annotation.ManageReadTransactional;
import wmkang.domain.annotation.ManageWriteTransactional;
import wmkang.domain.manage.entity.User;
import wmkang.domain.manage.repository.UserRepository;
import wmkang.task.dto.OpenUserControllerDto.RegistUser;


@RequiredArgsConstructor
@RequestMapping("/open/user")
@Transactional
@RestController
public class OpenUserController {


    private final UserRepository            userRepo;
    private final PasswordEncoder           passswdEncoder;


    @GetMapping("/")
    public Response<Void> open() {
        return Response.ok();
    }

    /**
     * 로그인
     */
    @ManageReadTransactional
    @GetMapping("/login")
    public Response<UserInfoDto> login(Authentication authentication) {
        if (authentication != null)
            return Response.ok(Util.copy(authentication.getPrincipal(), UserInfoDto.class));
        else
            return Status.ENTITY_NOT_EXIST.getResponse(null);
    }

    /**
     * 회원 가입
     */
    @ManageWriteTransactional
    @PostMapping("/register")
    public Response<UserInfoDto> register(@Valid RegistUser registUser) {

        registUser.validate();
        if (userRepo.findByEmail(registUser.getEmail()).isPresent()) {
            return Status.ENTITY_ALREADY_EXIST.getResponse(null);
        }

        User user = Util.copy(registUser, User.class);
        user.setPasswd(passswdEncoder.encode(user.getPasswd()));

        userRepo.save(user);
        return Response.ok(Util.copy(user, UserInfoDto.class));
    }
}
