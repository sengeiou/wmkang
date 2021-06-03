package wmkang.common.aop;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;
import wmkang.common.util.C;
import wmkang.common.util.Util;


@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {


    @Value("${app.debug.api.expose-error-message:false}")
    private boolean exposeMessage;


    /**
     * 컨트롤러 파라미터 타입에 맞지 않는 값이 전달되었을 때
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("EH_MethodArgumentTypeMismatchException> {} - {}", e.getClass().getName(), e.getMessage() );
        String message = Util.getMessage("typeMismatch.default", new Object[] {e.getValue()});
//        FIXME
//        String message = (e.getMessage().length() > 20)?
//                Util.getMessage("typeMismatch.default", new Object[] {e.getValue()})
//                : e.getMessage();
        List<String> fieldErrors = Arrays.asList(Util.getMessage(e.getErrorCode(), new Object[] { e.getName(), message }));
        return Status.PARAMETER_INVALID.getResponse(fieldErrors);
    }

    /**
     * 필수 파라미터 값이 전달되지 않았을 때
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<?> handleMissingServletRequestParameter(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error("EH_MissingServletRequestParameterException> : {} - {}", e.getClass().getName(), e.getMessage() );
        List<String> fieldErrors = Arrays.asList(Util.getMessage(C.MESSAGE_KEY_MISSING, e.getParameterName()));
        return Status.PARAMETER_INVALID.getResponse(fieldErrors);
    }

    /**
     * VO 필드 타입에 맞지 않는 값이 전달되었거나, Validator 조건에 맞지 않을 때
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public Response<?> handleInvalid(Exception e, HttpServletRequest request) {
        log.error("EH_BindException> {} - {}", e.getClass().getName(), e.getMessage() );
        BindingResult bindResult = null;
        if ((e instanceof BindException)) {
            bindResult = ((BindException) e).getBindingResult();
        } else {
            bindResult = ((MethodArgumentNotValidException) e).getBindingResult();
        }
        if (!bindResult.getFieldErrors().isEmpty()) {
            List<String> fieldErrors = bindResult.getFieldErrors().stream()
                    .map(fieldError -> {
                        if("ScriptAssert".equals(fieldError.getCodes()[3])) {
                            return Util.getMessage(fieldError.getDefaultMessage(), fieldError.getField());
                        } else {
                            return Util.getMessage(fieldError.getCodes()[3], fieldError.getField(), fieldError.getRejectedValue());
                        }
                    }).collect(Collectors.toList());

            return Status.PARAMETER_INVALID.getResponse(fieldErrors);
        }

        if (!bindResult.getGlobalErrors().isEmpty()) {
            List<String> globalErrors = bindResult.getGlobalErrors().stream()
                    .map(objectError -> Util.getMessage(objectError.getDefaultMessage()))
                    .collect(Collectors.toList());
            return Status.PARAMETER_INVALID.getResponse(globalErrors);
        }

        // FIXME FOR DEBUGGING
        return Status.PARAMETER_INVALID.getResponse(exposeMessage? Util.getMessage("Temp.Unexpected"): "");
    }

    /**
     * 쿼리 실행시 DataAccess에 실패했을 때
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public Response<?> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException e, HttpServletRequest request) {
        log.error("EH_InvalidDataAccessApiUsageException> {} - {}", e.getClass().getName(), e.getMessage() );
        if(e.getCause() instanceof IllegalArgumentException) {
            log.debug("> IllegalArgumentException");
        } else {
            log.debug("> " + e.getCause());
            // FIXME - FOR DEBUGGING
            return Status.PARAMETER_INVALID.getResponse(exposeMessage? Util.getMessage("Temp.Unexpected"): "");
        }
        // FIXME InvalidDataAccessApiUsageException과 ...IllegalArgumentException 과 연관이 맺어있지 않음.
        return null;
    }

    /**
     * 요청한 API에서 지원하지 않는 HTTP 메소드 요청
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("EH_HttpRequestMethodNotSupportedException> {} - {}", e.getClass().getName(), e.getMessage() );
        return Status.METHOD_NOT_ALLOWED.getResponse();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<?> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.error("EH_IllegalArgumentException> {} - {}", e.getClass().getName(), e.getMessage() );
        return Status.PARAMETER_INVALID.getResponse(exposeMessage? e.getMessage() : "");
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public Response<?> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.error("EH_AccessDeniedException> {} - {}", e.getClass().getName(), e.getMessage() );
        return Status.FORBIDDEN.getResponse(exposeMessage? e.getMessage() : "");
    }

    @ExceptionHandler(ApplicationException.class)
    public Response<?> handleApplication(ApplicationException e) {
        log.error("EH_ApplicationException> {}", e.getStatus() );
        return e.getResponse();
    }

//    @ResponseStatus(value=HttpStatus.FORBIDDEN)
//    public Response<?> OrderNotFoundException extends RuntimeException {
//        return null;
//    }

    @ExceptionHandler(Exception.class)
    public Response<?> handle(Exception e) {
        log.error("EH_Exception> {} - {}", e.getClass().getName(), e.getMessage() );
        e.printStackTrace();
        if(exposeMessage) {
            return Status.INTERNAL_SERVER_ERROR.getResponse(e.getMessage());
        } else {
            return Status.INTERNAL_SERVER_ERROR.getResponse();
        }
    }
}
