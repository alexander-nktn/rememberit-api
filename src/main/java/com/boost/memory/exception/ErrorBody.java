package com.boost.memory.exception;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

public class ErrorBody {
    @Getter
    private final Map<String, Object> errorData = new HashMap<>();
    private Exception ex;

    public ErrorBody(Exception ex) {
        this.ex = ex;
    }

    public ErrorBody prepare() {
        captureBasicExceptionInfo();
        captureTopStackTraceElement();
        captureCauseMessages();
        captureFullStackTrace();
        return this;
    }

    private void captureBasicExceptionInfo() {
        errorData.put("message", ex.getMessage());
        errorData.put("exceptionType", ex.getClass().getSimpleName());
    }

    private void captureTopStackTraceElement() {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement topElement = stackTrace[0];
            errorData.put("fileName", topElement.getFileName());
            errorData.put("lineNumber", topElement.getLineNumber());
            errorData.put("methodName", topElement.getMethodName());
        }
    }

    private void captureCauseMessages() {
        Throwable cause = ex.getCause();
        StringBuilder causeMessages = new StringBuilder();
        buildCauseMessages(cause, causeMessages);
        if (!causeMessages.isEmpty()) {
            errorData.put("cause", causeMessages.toString());
        }
    }

    private void buildCauseMessages(Throwable cause, StringBuilder causeMessages) {
        if (cause == null) return;
        if (!causeMessages.isEmpty()) {
            causeMessages.append(" -> ");
        }
        causeMessages.append(cause.getMessage());
        buildCauseMessages(cause.getCause(), causeMessages);
    }

    private void captureFullStackTrace() {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTraceElements) {
            sb.append(element.toString()).append("\n");
        }
        errorData.put("stackTrace", sb.toString());
    }
}
