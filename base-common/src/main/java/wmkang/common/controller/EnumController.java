package wmkang.common.controller;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.util.C;

/**
 * enum 타입 조회 API
 */
@Slf4j
@Profile("!release")
@RestController
public class EnumController {


    public static final String ALL_TYPE = "ALL";


    Map<String, Map<Integer, String>> enumMap;


    @GetMapping("/enum/{name}")
    public Response<?> getEnum(@PathVariable String name) {
        if (enumMap == null)
            scanEnum();

        if (ALL_TYPE.equals(name))
            return Response.ok(enumMap);

        Map<Integer, String> typeMap = enumMap.get(name);
        if (typeMap == null) {
            return Status.RESOURCE_NOT_EXIST.getResponse();
        } else {
            return Response.ok(typeMap);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void scanEnum() {
        enumMap = new HashMap<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(Enum.class));
        Set<BeanDefinition> candidates = provider.findCandidateComponents(C.PROJECT_BASE_PACKAGE);
        candidates.stream().forEach(d -> {
            try {
                Class<Enum> clazz = (Class<Enum>) Class.forName(d.getBeanClassName());
                EnumSet<?> enumSet = EnumSet.allOf(clazz);
                enumMap.put(clazz.getSimpleName(), enumSet.stream().collect(Collectors.toMap(Enum::ordinal, Enum::name)));
            } catch (Exception e) {
                log.error("Enum type loading failed", e);
            }
        });
    }
}
