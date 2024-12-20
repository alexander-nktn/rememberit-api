package rememberit.exception;

import lombok.Getter;
import rememberit.config.ServiceMethodContext;

@Getter
public class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final ServiceMethodContext context;

    public ApplicationException(String message, ErrorCode errorCode, ServiceMethodContext context) {
        super(message);
        this.errorCode = errorCode;
        this.context = context;
    }

    public ApplicationException(String message, ErrorCode errorCode, ServiceMethodContext context, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.context = context;
    }

    public String getRootCauseMessage() {
        Throwable root = getCause();
        return root != null ? root.getMessage() : null;
    }
}