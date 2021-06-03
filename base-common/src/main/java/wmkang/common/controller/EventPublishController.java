package wmkang.common.controller;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import wmkang.common.api.Response;
import wmkang.common.event.CodeRefreshEvent;

@Hidden
@RequiredArgsConstructor
@RequestMapping("/event")
@RestController
public class EventPublishController {


    private final ApplicationEventPublisher eventPublisher;


    @GetMapping("/code-refresh")
    public Response<Void> refresh() {
        eventPublisher.publishEvent(new CodeRefreshEvent(this));
        return Response.ok();
    }
}
