package wmkang.gateway;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import wmkang.common.api.Response;


@Slf4j
@Order(-1)
@RequiredArgsConstructor
@Component
public class ExceptionHandler implements ErrorWebExceptionHandler {


    public static final String GATEWAY_ERROR_MESSAGE_PREFIX = "[GATEWAY] ";

    private final ObjectMapper objectMapper;

    @Value("${app.debug.api.expose-error-message:false}")
    boolean                    exposeMessage;


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {

        ServerHttpResponse httpResponse = exchange.getResponse();

        if (httpResponse.isCommitted()) {
            return Mono.error(throwable);
        }

        if (throwable instanceof ResponseStatusException) {

            httpResponse.setStatusCode(((ResponseStatusException) throwable).getStatus());

        } else if (httpResponse.getStatusCode() == HttpStatus.OK) {

            log.error("# ERROR> {}", (throwable != null) ? throwable.getClass().getName() : "NULL");
            httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        httpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return httpResponse.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = httpResponse.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes( Response.getResponse(httpResponse.getStatusCode().value(),
                            GATEWAY_ERROR_MESSAGE_PREFIX + (((exposeMessage == true) && (throwable != null))? throwable.getMessage() : "")
                        )));
            } catch (JsonProcessingException e) {
                log.error("Error writing response", throwable);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
