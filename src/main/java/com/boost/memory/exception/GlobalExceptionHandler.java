package com.boost.memory.exception;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        logger.error("Entity not found", ex);
        ErrorBody errorBody = new ErrorBody(ex);
        return new ResponseEntity<>(
                errorBody.prepare(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
        logger.error("Runtime exception", ex);
        ErrorBody errorBody = new ErrorBody(ex);
        return new ResponseEntity<>(
                errorBody.prepare(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> handleEntityExistsException(EntityExistsException ex, WebRequest request) {
        logger.error("Entity exists", ex);
        ErrorBody errorBody = new ErrorBody(ex);
        return new ResponseEntity<>(
                errorBody.prepare(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Entity exists", ex);
        ErrorBody errorBody = new ErrorBody(ex);
        return new ResponseEntity<>(
                errorBody.prepare(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Internal server error", ex);
        ErrorBody errorBody = new ErrorBody(ex);
        return new ResponseEntity<>(
                errorBody.prepare(),
                HttpStatus.NOT_FOUND
        );
    }
}
