package wmkang.common.aop;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import wmkang.common.annotation.AuditLog;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.util.C;
import wmkang.common.util.Util;
import wmkang.domain.service.entity.AuditHistory;
import wmkang.domain.service.repository.AuditHistoryRepository;


@RequiredArgsConstructor
@Aspect
@Component
public class AuditLogAdvice {


    @Value("${app.audit.enable}")
    private boolean auditEnabled;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditHistoryRepository auditHsitoryRepo;


    @PostConstruct
    public void init(){
        objectMapper = objectMapper.copy();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }


    @AfterReturning(pointcut = C.AOP_POINTCUT_EXPR_CONTROLLER + "&& @annotation(auditLog)", returning = "response")
    public void processAudit(JoinPoint joinPoint, Response<?> response, AuditLog auditLog) {

        if ((!auditEnabled) || (response == null) || (response.getHeader() != Status.SUCCESS)) {
            return;
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < paramValues.length; i++) {
            if (paramValues[i].getClass().getName().startsWith("javax"))
                continue;
            paramMap.put(paramNames[i], paramValues[i]);
        }

        String paramJsonStr = null;
        try {
            paramJsonStr = objectMapper.writeValueAsString(paramMap);
        } catch (Exception e) {
            paramJsonStr = "Parameters serialization failed";
        }

        AuditHistory history = AuditHistory.builder().api(auditLog.id())
                                                     .action(auditLog.action())
                                                     .args(paramJsonStr)
                                                     .actor(Util.getLoginUser().getId())
                                                     .time(LocalDateTime.now())
                                                     .build();
        auditHsitoryRepo.save(history);
    }
}
