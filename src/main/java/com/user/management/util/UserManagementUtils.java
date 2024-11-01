package com.user.management.util;

import com.user.management.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserManagementUtils {

    public static <T> ResponseEntity<ApiResponse<T>> successResponse(String message, HttpStatus status, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(message, status.value(), data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> errorResponse(Throwable e) {
        throw new RuntimeException(e);
    }
}
