package wmkang.task.controller;


import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wmkang.common.api.Response;
import wmkang.common.security.UserInfoDto;
import wmkang.common.util.Util;


@RequestMapping("/my")
@RestController
@RequiredArgsConstructor
public class MyController {


    /**
     * 본인 정보 조회
     */
    @GetMapping
    public Response<UserInfoDto> myInfo(Authentication authentication) {
        return Response.ok(Util.copy(authentication.getPrincipal(), UserInfoDto.class));
    }

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public Response<Void> logout(HttpSession session) {
        session.invalidate();
        return Response.ok();
    }
}
