package wmkang.common.security;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import wmkang.common.api.Status;


@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {


    @Value("${spring.application.name}")
    private String       applicationName;

    private final String unAuthRespStr;


    public AuthenticationEntryPoint(ObjectMapper objectMapper, MessageSourceAccessor messageAccessor) throws Exception {
        unAuthRespStr = objectMapper.writeValueAsString(Status.UNAUTHORIZED.getResponse());
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName(applicationName);
        super.afterPropertiesSet();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(Status.UNAUTHORIZED.getCode());
        response.getWriter().write(unAuthRespStr);
    }
}
