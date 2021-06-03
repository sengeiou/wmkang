package wmkang.common.aop;


import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.util.C;
import wmkang.domain.util.ShardHolder;


@Aspect
@Component
public class ControllerAdvice {


    @AfterReturning(pointcut = C.AOP_POINTCUT_EXPR_CONTROLLER, returning = "response")
    public void handleHttpStatus(JoinPoint joinPoint, Response<?> response) {
        if ((response != null) && (response.getHeader() != Status.SUCCESS)) {
            try {
                HttpServletResponse httpResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
                httpResponse.setStatus(response.getHeader().getCode());
            } catch (Exception e) {
            }
        }
        ShardHolder.remove();
    }
}
