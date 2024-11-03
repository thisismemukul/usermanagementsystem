package com.user.management.exceptions;

import com.user.management.response.ApiResponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.TimeoutException;

import static com.user.management.constants.Constants.*;
import static com.user.management.enums.ResponseCode.*;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<DefaultBaseError<?>>> handleValidationException(ValidationException ex) {
        return buildErrorResponse(
                ex.getIBaseError().getErrorCode(),
                ex.getIBaseError().getErrorMessage(),
                ex.getIBaseError().getUserMessage(),
                VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                ex.getIBaseError().displayMsg()
        );
    }

    @ExceptionHandler(UserMgmtException.class)
    public ResponseEntity<ApiResponse<DefaultBaseError<?>>> handleUserMgmtException(UserMgmtException ex) {
        return buildErrorResponse(
                ex.getIBaseError().getErrorCode(),
                ex.getIBaseError().getErrorMessage(),
                ex.getIBaseError().getUserMessage(),
                USER_MGMT_SERVICE_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                ex.getIBaseError().displayMsg()
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<DefaultBaseError<?>>> handleNullPointerException(NullPointerException ex) {
        return buildErrorResponse(
                NULL_POINTER_EXCEPTION.code(),
                NULL_POINTER_EXCEPTION.message() + ex.getMessage(),
                NULL_POINTER_EXCEPTION.userMessage(),
                NULL_POINTER,
                HttpStatus.INTERNAL_SERVER_ERROR,
                NULL_POINTER_EXCEPTION.message(),
                false
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<DefaultBaseError<?>>> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(
                RUNTIME_EXCEPTION.code(),
                RUNTIME_EXCEPTION.message() + ex.getMessage(),
                RUNTIME_EXCEPTION.userMessage(),
                RUNTIME_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                RUNTIME_EXCEPTION.message(),
                false
        );
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<DefaultBaseError<?>>> handleServiceException(ServiceException ex) {
        return buildErrorResponse(
                SERVICE_EXCEPTION.code(),
                SERVICE_EXCEPTION.message() + ex.getMessage(),
                SERVICE_EXCEPTION.userMessage(),
                SERVICE_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                SERVICE_EXCEPTION.message(),
                false
        );
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<DefaultBaseError<?>>> handleTimeoutException(TimeoutException ex) {
        return buildErrorResponse(
                TIMEOUT_EXCEPTION.code(),
                TIMEOUT_EXCEPTION.message() + ex.getMessage(),
                TIMEOUT_EXCEPTION.userMessage(),
                TIMEOUT_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR,
                TIMEOUT_EXCEPTION.message(),
                false
        );
    }

    private ResponseEntity<ApiResponse<DefaultBaseError<?>>> buildErrorResponse(
            String errorCode, String errorMessage, String userMessage,
            String errorType, HttpStatus status, String apiMessage, boolean displayMsg
    ) {
        DefaultBaseError<?> errorResponse = new DefaultBaseError<>(
                errorCode, errorMessage, userMessage, errorType, displayMsg
        );
        ApiResponse<DefaultBaseError<?>> apiResponse = new ApiResponse<>(
                apiMessage, status.value(), errorResponse
        );
        return ResponseEntity.status(status).body(apiResponse);
    }
}
