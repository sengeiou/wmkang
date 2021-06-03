package wmkang.gateway;


import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import wmkang.common.controller.RootController;


@Configuration
@RequiredArgsConstructor
public class GatewayConfig {


    private final RouteDefinitionLocator locator;

    @Bean
    public RootController rootController() {
        return new RootController();
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        definitions.stream().filter(routeDefinition -> routeDefinition.getId().startsWith("svc-")).forEach(routeDefinition -> {
            String name = routeDefinition.getId().replaceAll("svc-", "");
            groups.add(GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").setGroup(name).build());
        });
        return groups;
    }

}
