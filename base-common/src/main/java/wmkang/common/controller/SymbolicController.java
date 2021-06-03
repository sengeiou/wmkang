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
import wmkang.domain.enums.Symbolic;

/**
 * Symbolic enum 타입 조회 API
 */
@Slf4j
@Profile("!release")
@RestController
public class SymbolicController {


    public static final String ALL_TYPE = "ALL";


    Map<String, Map<String, String>> symblicMap;


    @GetMapping("/symbol/{name}")
    public Response<?> getSymbolic(@PathVariable String name) {
        if (symblicMap == null)
            scanSymbolic();
        if (ALL_TYPE.equals(name))
            return Response.ok(symblicMap);
        Map<String, String> typeMap = symblicMap.get(name);
        if (typeMap == null) {
            return Status.RESOURCE_NOT_EXIST.getResponse();
        } else {
            return Response.ok(typeMap);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void scanSymbolic() {
        symblicMap = new HashMap<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(Symbolic.class));
        Set<BeanDefinition> candidates = provider.findCandidateComponents(C.PROJECT_BASE_PACKAGE);
        candidates.stream().forEach(d -> {
            try {
                Class<Enum> clazz = (Class<Enum>) Class.forName(d.getBeanClassName());
                EnumSet<?> enumSet = EnumSet.allOf(clazz);
                symblicMap.put(clazz.getSimpleName(), enumSet.stream().collect(Collectors.toMap(e -> ((Symbolic)e).getSymbol(), Enum::name)));
            } catch (Exception e) {
                log.error("Symbolic type loading failed", e);
            }
        });
    }
}
