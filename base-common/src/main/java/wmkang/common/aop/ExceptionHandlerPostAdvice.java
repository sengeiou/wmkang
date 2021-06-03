package wmkang.common.aop;


import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import wmkang.common.api.Response;
import wmkang.domain.util.ShardHolder;


@Aspect
@Component
public class ExceptionHandlerPostAdvice {

    @AfterReturning(pointcut = "execution(wmkang.common.api.Response wmkang.common.aop.ExceptionHandlerAdvice.*(..))", returning = "response")
    public void handleHttpStatus(JoinPoint joinPoint, Response<?> response) {
        HttpServletResponse httpResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        httpResponse.setStatus(response.getHeader().getCode());
        ShardHolder.remove();
    }
}
