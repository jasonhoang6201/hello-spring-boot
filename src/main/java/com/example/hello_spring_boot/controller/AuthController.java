package com.example.hello_spring_boot.controller;

import com.example.hello_spring_boot.dto.request.ApiResponse;
import com.example.hello_spring_boot.dto.request.IntrospectRequest;
import com.example.hello_spring_boot.dto.request.LoginRequest;
import com.example.hello_spring_boot.dto.request.RefreshTokenRequest;
import com.example.hello_spring_boot.dto.response.IntrospectResponse;
import com.example.hello_spring_boot.dto.response.LoginResponse;
import com.example.hello_spring_boot.dto.response.UserResponse;
import com.example.hello_spring_boot.service.AuthService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/auth")
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> handleLogin(@Valid @RequestBody LoginRequest request) {
        log.warn("handleLogin");
        return ApiResponse.<LoginResponse>builder()
                .result(authService.handleLogin(request))
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> handleIntrospect(@Valid @RequestBody IntrospectRequest request) {
        return ApiResponse.<IntrospectResponse>builder()
                .result(IntrospectResponse
                        .builder()
                        .valid(authService.introspect(request))
                        .build()
                )
                .build();
    }

    @GetMapping("me")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(authService.getMyInfo())
                .build();
    }

    @PostMapping("/refreshToken")
    ApiResponse<LoginResponse> handleRefreshToken(@RequestBody RefreshTokenRequest request) throws ParseException, JOSEException {
        log.warn("vao dayyyy");
        return ApiResponse.<LoginResponse>builder()
                .result(this.authService.handleRefreshToken(request))
                .build();
    }
}
