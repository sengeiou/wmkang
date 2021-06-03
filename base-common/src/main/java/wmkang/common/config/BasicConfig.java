package wmkang.common.config;


import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import wmkang.common.cache.CacheHandler;
import wmkang.common.event.CodeRefreshEvent;
import wmkang.common.file.UploadFileUtil;
import wmkang.common.security.UserDetails;
import wmkang.common.util.Util;
import wmkang.domain.enums.converter.ActionTypeConverter;
import wmkang.domain.enums.converter.FileCategoryConverter;
import wmkang.domain.enums.converter.GenderConverter;
import wmkang.domain.enums.converter.RoleConverter;
import wmkang.domain.enums.converter.ShardConverter;


@Configuration
public class BasicConfig {


    @Autowired
    private CacheHandler cacheHandler;


    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource);
    }

    @ControllerAdvice
    public static class ApiControllerAdvice {

        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        }
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer( @Value("${spring.mvc.format.date}")       String   dateFormat,
                                              @Value("${spring.mvc.format.time}")       String   timeFormat,
                                              @Value("${spring.mvc.format.date-time}")  String   dateTimeFormat) {
        return new WebMvcConfigurer() {

            @Override
            public void addFormatters(FormatterRegistry registry) {
                // LocalDate, LocalTime, LocalDateTime
                DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
                registrar.setDateFormatter(DateTimeFormatter.ofPattern(dateFormat));
                registrar.setTimeFormatter(DateTimeFormatter.ofPattern(timeFormat));
                registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(dateTimeFormat));
                registrar.registerFormatters(registry);
                // Enums
                registry.addConverter(new GenderConverter());
                registry.addConverter(new RoleConverter());
                registry.addConverter(new ShardConverter());
                registry.addConverter(new ActionTypeConverter());
                registry.addConverter(new FileCategoryConverter());
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer( @Value("${spring.mvc.format.date}")      String dateFormat,
                                                                    @Value("${spring.mvc.format.time}")      String timeFormat,
                                                                    @Value("${spring.mvc.format.date-time}") String dateTimeFormat) {
        return builder -> {
            // LocalDate
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.deserializers(new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormat)));
            // LocalTime
            builder.serializers(new LocalTimeSerializer(DateTimeFormatter.ofPattern(timeFormat)));
            builder.deserializers(new LocalTimeDeserializer(DateTimeFormatter.ofPattern(timeFormat)));
            // LocalDateTime
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
            builder.deserializers(new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }

    @Async
    @EventListener({ ApplicationStartedEvent.class, CodeRefreshEvent.class })
    public void refreshCodes(ApplicationEvent e) {
        cacheHandler.reloadCodeCache();
        if(e instanceof ApplicationStartedEvent) {
            DefaultCookieSerializer cookieSerializer = ((ApplicationStartedEvent)e).getApplicationContext().getBean(DefaultCookieSerializer.class);
            cookieSerializer.setSameSite("none");
        }
    }

    @Bean
    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    public AuditorAware<Integer> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if ((authentication == null) || (authentication instanceof AnonymousAuthenticationToken))
                return Optional.empty();
            return Optional.of(((UserDetails) authentication.getPrincipal()).getId());
        };
    }

    @Autowired
    public void init( @Value("${app.file.upload.base-path}")   String                uploadBasePath,
                      @Value("${server.servlet.context-path}") String                contextPath,
                                                               MessageSourceAccessor messageAccessor) {
        Util.init(messageAccessor);
        UploadFileUtil.init(uploadBasePath, contextPath);
    }

    @Bean
    public OpenAPI customOpenAPI( @Value("${info.app.name}")           String title,
                                  @Value("${info.app.description}")    String description,
                                  @Value("${info.app.version}")        String version) {
        return new OpenAPI().info(new Info().title(title)
                            .version(version)
                            .description(description)
                            .termsOfService("http://swagger.io/terms/")
                            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
