package com.example.hello_spring_boot.exception;

import com.example.hello_spring_boot.dto.request.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private final String MIN_ATTRIBUTE_NAME = "min";
    private final String MAX_ATTRIBUTE_NAME = "max";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleException(RuntimeException e) {
        ApiResponse<String> apiResponse = new ApiResponse<String>();
        apiResponse.setMessage(e.getMessage());
        apiResponse.setCode(ErrorCode.UNKNOW_ERROR.getCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<String> apiResponse = new ApiResponse<String>();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.UNKNOW_ERROR;
        Map<String, Objects> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            var constrainValidation = e.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = constrainValidation.getConstraintDescriptor().getAttributes();
        } catch (IllegalArgumentException ex) {

        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(
                Objects.isNull(attributes) ? errorCode.getMessage() : this.mapMessage(errorCode.getMessage(), attributes));
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setField(e.getFieldError().getField());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    private String mapMessage(String message, Map<String, Objects> attributes) {
        String min = Objects.toString(attributes.get(MIN_ATTRIBUTE_NAME));
        return message.replace("{" + MIN_ATTRIBUTE_NAME + "}", min);
    }
}
