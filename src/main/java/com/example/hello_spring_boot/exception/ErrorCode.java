package com.example.hello_spring_boot.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_EXISTED(1002, "User existed"),
    RECORD_EXISTED(1002, "Record existed"),
    UNKNOW_ERROR(9999, "Unregister erorr"),
    INVALID_PASSWORD(1003, "Invalid password"),
    INVALID_USERNAME(1004, "Invalid username"),
    USER_NOTFOUND(1005, "User Not Found"),
    UNAUTHORIZED(1006, "You don't have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    REQUIRE_FIELD(1008, "This field is required", HttpStatus.BAD_REQUEST),
    DOB_INVALID(1009, "Age must be at least {min}", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatus httpStatus;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
