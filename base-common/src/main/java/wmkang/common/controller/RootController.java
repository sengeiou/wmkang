package wmkang.common.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import wmkang.common.api.Response;

@Hidden
@RestController
public class RootController {


    @Value("${spring.application.name}")
    String applicationName;


    @RequestMapping(value = {"/", "/healthz"})
    public Response<String> root() {
        return Response.ok(applicationName);
    }
}
