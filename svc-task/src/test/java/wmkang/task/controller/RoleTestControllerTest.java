package wmkang.task.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import wmkang.common.api.Status;
import wmkang.common.test.AbstractWebMvcTest;

/**
 * RoleTestController 단위 테스트 클래스
 */
public class RoleTestControllerTest extends AbstractWebMvcTest {


    @DisplayName("사용자 권한 테스트 - 'USER' Role")
    @WithUserDetails("user1@wmkang.com")
    @Test
    public void testAsUser() throws Exception {

        // 1. URL 권한 설정 테스트
        mvc.perform(userRoleRequest()).andExpect(status().isOk());
        mvc.perform(managerRoleRequest()).andExpect(status().is(Status.FORBIDDEN.getCode()));
        mvc.perform(adminRoleRequest()).andExpect(status().is(Status.FORBIDDEN.getCode()));

        // 2. 메소드 권한 설정 테스트
        mvc.perform(hasUserRoleRequest()).andExpect(status().isOk());
        mvc.perform(hasManagerRoleRequest()).andExpect(status().is(Status.FORBIDDEN.getCode()));
        mvc.perform(hasAdminRoleRequest()).andExpect(status().is(Status.FORBIDDEN.getCode()));
    }


    @DisplayName("사용자 권한 테스트 - 'MANAGER' Role")
    @WithUserDetails("manager@wmkang.com")
    @Test
    public void testAsManager() throws Exception {

        // 1. URL 권한 설정 테스트
        mvc.perform(userRoleRequest()).andExpect(status().isOk());
        mvc.perform(managerRoleRequest()).andExpect(status().isOk());
        mvc.perform(adminRoleRequest()).andExpect(status().is(Status.FORBIDDEN.getCode()));

        // 2. 메소드 권한 설정 테스트
        mvc.perform(hasUserRoleRequest()).andExpect(status().isOk());
        mvc.perform(hasManagerRoleRequest()).andExpect(status().isOk());
        mvc.perform(hasAdminRoleRequest()).andExpect(status().is(Status.FORBIDDEN.getCode()));
    }


    @DisplayName("사용자 권한 테스트 - 'ADMIN' Role")
    @WithUserDetails("admin@wmkang.com")
    @Test
    public void testAsAdmin() throws Exception {

        // 1. URL 권한 설정 테스트
        mvc.perform(userRoleRequest()).andExpect(status().isOk());
        mvc.perform(managerRoleRequest()).andExpect(status().isOk());
        mvc.perform(adminRoleRequest()).andExpect(status().isOk());

        // 2. 메소드 권한 설정 테스트
        mvc.perform(hasUserRoleRequest()).andExpect(status().isOk());
        mvc.perform(hasManagerRoleRequest()).andExpect(status().isOk());
        mvc.perform(hasAdminRoleRequest()).andExpect(status().isOk());
    }

    //------------------------------------------------------------------------------------
    // 테스트 컨트롤러 각 메서드별 HTTP 요청 생성 메소드

    private MockHttpServletRequestBuilder userRoleRequest() {
        return get("/test/role/user/").accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder managerRoleRequest() {
        return get("/test/role/manager/").accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder adminRoleRequest() {
        return get("/test/role/admin/").accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder hasUserRoleRequest() {
        return get("/test/role/method/user").accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder hasManagerRoleRequest() {
        return get("/test/role/method/manager").accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder hasAdminRoleRequest() {
        return get("/test/role/method/admin").accept(MediaType.APPLICATION_JSON_VALUE);
    }
}

