package wmkang.common.config;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import wmkang.common.aop.ExceptionHandlerAdvice;
import wmkang.common.util.C;
import wmkang.domain.enums.Switch;
import wmkang.domain.enums.Type;

/**
 * 각종 디버깅 기능들을 정의하고, 설정값에 따라 선택적으로 동작하도록 구성함.
 * (@Profile 설정으로 release 프로파일에서는 동작하지 않도록 설정함)
 */
@Slf4j
@Profile("!release")
@Aspect
@Configuration
public class DebugConfig {


    @Value("${app.debug.log.parameters.http:OFF}")
    private Switch logHttpParameters;

    @Value("${app.debug.log.parameters.controller:OFF}")
    private Switch logControllerParameters;

    @Value("${app.debug.log.classpath:OFF}")
    private Switch logClassPath;

    @Value("${app.debug.log.beans.switch:OFF}")
    private Switch logBeans;

    @Value("${app.debug.log.beans.type:OBJECT}")
    private Type   logBeansType;

    @Autowired
    private ObjectMapper objectMapper;


    @PostConstruct
    public void init(){
        objectMapper = objectMapper.copy();
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * HTTP 파라미터 출력
     */
    @Bean
    public Filter printingHttpParametersFilter() {
        return new Filter() {

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                if(logHttpParameters == Switch.ON) {
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    System.out.println();
                    log.debug("▶ {}> {}", httpRequest.getMethod(), httpRequest.getRequestURI());
                    AtomicInteger idx = new AtomicInteger();
                    Collections.list(request.getParameterNames()).stream().sorted()
                            .forEach(p -> log.debug(String.format("%3s. %s = '%s'", idx.incrementAndGet(), p, request.getParameter(p.toString()))));
                }
                chain.doFilter(request, response);
            }
        };
    }

    /**
     * Controller 메소드 파라미터 출력
     */
    @Before(C.AOP_POINTCUT_EXPR_CONTROLLER)
    public void printControllerParameters(JoinPoint joinPoint) throws JsonProcessingException {
        if(logControllerParameters == Switch.ON) {
            if(joinPoint.getSignature().getDeclaringType() == ExceptionHandlerAdvice.class)
                return;

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = methodSignature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();

            log.debug("▶ {}.{}()", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName()) ;

            for (int i = 0; i < paramValues.length; i++) {
                if((paramValues[i] == null) || paramValues[i].getClass().getName().startsWith("javax")
                                            || paramValues[i].getClass().getName().startsWith("org."))
                    continue;
                try {
                    log.debug("  {}. {} = {}", (i+1), paramNames[i], objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramValues[i]));
                } catch(Exception e) {
                    log.debug("  {}. {} = {}", (i+1), paramNames[i], "Failed writing - " + paramValues[i].getClass().getName());
                }
            }
        }
    }

    /**
     * JDBC Connection 취득 모니터링
     */
//    @AfterReturning(pointcut = C.AOP_POINTCUT_EXPR_JDBC, returning = "connection")
    public void datasourceConnection(java.sql.Connection connection) {
        try {
            log.info("▶ JDBC : {}", connection.getMetaData().getURL());
        }catch(Exception e) {
        }
    }

    /**
     * 애플리케이션 구동 시, Spring 빈 목록  & CLASAPTH 항목 출력
     */
    @EventListener(ContextRefreshedEvent.class)
    public void printEnvironments(ApplicationEvent e) {

        AbstractApplicationContext context = (AbstractApplicationContext) e.getSource();

        if (logBeans == Switch.ON) {
            StringBuilder sb = new StringBuilder(C.LINE_CHAR).append("▶  SPRING-BEANS :").append(C.LINE_CHAR);
            AtomicInteger idx = new AtomicInteger();
            Arrays.stream(context.getBeanDefinitionNames()).sorted().forEach(n -> {
                String bean = null;
                try {
                    if (logBeansType == Type.OBJECT) {
                        bean = context.getBean(n).toString();
                    } else {
                        bean = context.getBean(n).getClass().getName();
                    }
                } catch (Exception x) {
                } finally {
                    sb.append(String.format("%3s. %s = %s%n", idx.incrementAndGet(), n, bean));
                }
            });
            log.debug(sb.toString());
        }

        if (logClassPath == Switch.ON) {
            String classPath = System.getProperty("java.class.path");
            Stream<String> stream;
            if (C.IS_WINDOWS) {
                stream = Pattern.compile(";").splitAsStream(classPath);
            } else {
                stream = Pattern.compile(":").splitAsStream(classPath);
            }
            StringBuilder sb = new StringBuilder(C.LINE_CHAR).append("▶ CLASSPATH :").append(C.LINE_CHAR);
            AtomicInteger idx = new AtomicInteger();
            stream.sorted().forEach(p -> sb.append(String.format("%3d. %s%n", idx.incrementAndGet(), p) ));
            log.debug(sb.toString());
        }
    }
}
