package wmkang.task.test;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wmkang.common.api.Response;


@RequiredArgsConstructor
@RequestMapping("/test/role")
@RestController
public class RoleTestController {


    //----------------------------------
    // 1. URL 권한 설정 테스트
    //  - URL 권한 설정   : wmkang.common.config.SecurityConfig.configure()
    //  - 권한 계층 관계 설정 : wmkang.common.config.SecurityConfig.roleHierarchy()

    @GetMapping("/user/")
    public Response<Void> userRole() {
        return Response.ok();
    }

    @GetMapping("/manager/")
    public Response<Void> managerRole() {
        return Response.ok();
    }

    @GetMapping("/admin/")
    public Response<Void> adminRole() {
        return Response.ok();
    }

    //----------------------------------
    // 2. 메소드 권한 설정 테스트

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/method/user")
    public Response<Void> hasUserRole() {
        return Response.ok();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/method/manager")
    public Response<Void> hasManagerRole() {
        return Response.ok();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/method/admin")
    public Response<Void> hasAdminRole() {
        return Response.ok();
    }
}
