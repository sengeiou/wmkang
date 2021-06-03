package wmkang.common.exception;


import lombok.Getter;
import wmkang.common.api.Response;
import wmkang.common.api.Status;

@SuppressWarnings("serial")
@Getter
public class ApplicationException extends RuntimeException {


    private final           Status status;
    private final transient Object data;


    public ApplicationException(Status status) {
        this.status = status;
        this.data = null;
    }

    public ApplicationException(Status status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Response<?> getResponse() {
        return (data == null)? status.getResponse() : status.getResponse(data);
    }
}
