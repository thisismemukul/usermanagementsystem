package com.user.management.util;

import com.user.management.enums.ResponseCode;
import com.user.management.exceptions.DefaultBaseError;
import com.user.management.exceptions.IBaseError;
import com.user.management.exceptions.UserMgmtException;
import com.user.management.exceptions.ValidationException;
import com.user.management.response.ApiResponse;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

import java.util.function.Supplier;

import static com.user.management.enums.ResponseCode.CONTENT_IS_EMPTY;
import static com.user.management.enums.ResponseCode.USER_DETAILS_MISSING;

/**
 * Utility class providing methods for handling API responses, error handling, and validation
 * in the user management system.
 */
public class UserManagementUtils {

    /**
     * Handles response formatting and manages any exceptions that may occur during data retrieval.
     *
     * @param supplier       Lambda expression to provide the data to be included in the response.
     * @param successMessage Success message to be included in the response body.
     * @param status         HTTP status code to set for the response.
     * @param <T>            Type of data included in the response body.
     * @return ResponseEntity containing ApiResponse with data if successful, or an error message if an exception occurs.
     */
    public static <T> ResponseEntity<ApiResponse<T>> handleResponse(Supplier<T> supplier, String successMessage, HttpStatus status) {
        try {
            T result = supplier.get();
            return UserManagementUtils.successResponse(successMessage, status, result);
        } catch (Throwable e) {
            return UserManagementUtils.errorResponse(e);
        }
    }

    /**
     * Constructs a successful API response.
     *
     * @param message Success message describing the result of the operation.
     * @param status  HTTP status code indicating the success state.
     * @param data    Data to be included in the response.
     * @param <T>     Type of the data in the response.
     * @return A ResponseEntity containing ApiResponse with the specified message, status, and data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> successResponse(String message, HttpStatus status, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(message, status.value(), data));
    }

    /**
     * Handles errors by mapping specific exceptions to custom error responses, ensuring that meaningful
     * error information is returned to the client.
     *
     * @param e The exception encountered during processing.
     * @param <T> Type parameter for the ApiResponse's data.
     * @return ResponseEntity containing an ApiResponse with relevant error details.
     *
     * @exception ValidationException Thrown when a validation-related error occurs.
     * @exception UserMgmtException Thrown when a user management-specific error occurs.
     * @exception ServiceException Thrown when a service layer error arises.
     * @exception RuntimeException Thrown for other runtime exceptions.
     */
    public static <T> ResponseEntity<ApiResponse<T>> errorResponse(Throwable e) {
        if (e instanceof ValidationException validationException) {
            IBaseError<?> error = validationException.getIBaseError();
            throw new ValidationException(error);
        } else if (e instanceof UserMgmtException userMgmtException) {
            IBaseError<?> error = userMgmtException.getIBaseError();
            throw new UserMgmtException(error);
        } else if (e instanceof ServiceException) {
            throw new ServiceException(e.getMessage(), e);
        } else if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new ValidationException based on a provided ResponseCode.
     *
     * @param code The ResponseCode representing the validation error.
     * @return A new instance of ValidationException containing details from the ResponseCode.
     */
    public static ValidationException createValidationException(ResponseCode code) {
        return new ValidationException(
                new DefaultBaseError<>(
                        code.code(),
                        code.message(),
                        code.userMessage()
                )
        );
    }

    /**
     * Creates a new UserMgmtException based on a provided ResponseCode.
     *
     * @param code The ResponseCode representing the user management error.
     * @return A new instance of UserMgmtException containing details from the ResponseCode.
     */
    public static UserMgmtException createUserMgmtException(ResponseCode code) {
        return new UserMgmtException(
                new DefaultBaseError<>(
                        code.code(),
                        code.message(),
                        code.userMessage()
                )
        );
    }

    /**
     * Validates the presence of content and user details, throwing an appropriate exception if either is missing.
     *
     * @param content      The content to validate.
     * @param userDetails  The user details to validate.
     * @exception ValidationException Thrown if content is empty.
     * @exception UserMgmtException Thrown if user details are missing.
     */
    public static void validateContentAndUser(String content, UserDetails userDetails) {
        if (ObjectUtils.isEmpty(content)) {
            throw createValidationException(CONTENT_IS_EMPTY);
        }
        validateUserDetails(userDetails);
    }

    /**
     * Validates the presence of note ID, content, and user details, throwing an exception if any are missing.
     *
     * @param noteId       The note ID to validate.
     * @param content      The content to validate.
     * @param userDetails  The user details to validate.
     * @exception ValidationException Thrown if note ID or content is empty.
     * @exception UserMgmtException Thrown if user details are missing.
     */
    public static void validateNoteIdContentAndUser(Long noteId, String content, UserDetails userDetails) {
        if (ObjectUtils.isEmpty(noteId) || ObjectUtils.isEmpty(content)) {
            throw createValidationException(CONTENT_IS_EMPTY);
        }
        validateUserDetails(userDetails);
    }

    /**
     * Validates the presence of keep ID and user details, throwing an exception if either is missing.
     *
     * @param keepId       The keep ID to validate.
     * @param userDetails  The user details to validate.
     * @exception ValidationException Thrown if keep ID is empty.
     * @exception UserMgmtException Thrown if user details are missing.
     */
    public static void validateKeepIdAndUser(Long keepId, UserDetails userDetails) {
        if (ObjectUtils.isEmpty(keepId)) {
            throw createValidationException(CONTENT_IS_EMPTY);
        }
        validateUserDetails(userDetails);
    }

    /**
     * Validates the presence of user details, throwing an exception if missing.
     *
     * @param userDetails The user details to validate.
     * @exception UserMgmtException Thrown if user details are missing or invalid.
     */
    public static void validateUserDetails(UserDetails userDetails) {
        if (ObjectUtils.isEmpty(userDetails.getUsername())) {
            throw createUserMgmtException(USER_DETAILS_MISSING);
        }
    }

}
