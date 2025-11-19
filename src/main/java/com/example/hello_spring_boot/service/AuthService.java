package com.example.hello_spring_boot.service;

import com.example.hello_spring_boot.dto.request.IntrospectRequest;
import com.example.hello_spring_boot.dto.request.LoginRequest;
import com.example.hello_spring_boot.dto.request.RefreshTokenRequest;
import com.example.hello_spring_boot.dto.response.LoginResponse;
import com.example.hello_spring_boot.dto.response.UserResponse;
import com.example.hello_spring_boot.entity.RefreshToken;
import com.example.hello_spring_boot.entity.User;
import com.example.hello_spring_boot.exception.AppException;
import com.example.hello_spring_boot.exception.ErrorCode;
import com.example.hello_spring_boot.mapper.UserMapper;
import com.example.hello_spring_boot.repository.RefreshTokenRepository;
import com.example.hello_spring_boot.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    RefreshTokenRepository refreshTokenRepository;

    @NonFinal
    @Value("${spring.jwt.secret}")
    protected String jwtKey;

    @NonFinal
    @Value("${spring.jwt.refresh_secret}")
    protected String jwtRefreshKey;

    public LoginResponse handleLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        // check password
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (matches) {

            return LoginResponse.builder()
                    .accessToken(this.generateToken(user))
                    .refreshToken(this.generateRefreshToken(user))
                    .build();
        } else throw new AppException(ErrorCode.UNAUTHORIZED);

    }

    public boolean introspect(IntrospectRequest request) {
        try {
            JWSVerifier verifier = new MACVerifier(jwtKey.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(request.getToken());
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return signedJWT.verify(verifier) && expiration.after(new Date());
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private String signToken(JWSHeader header, JWTClaimsSet jwtClaimsSet, String jwtKey) {
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("hello-spring")
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", scopeBuilder(user))
                .build();

        return signToken(header, jwtClaimsSet, jwtKey);
    }

    private String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("hello-spring")
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .build();

        String token = signToken(header, jwtClaimsSet, jwtRefreshKey);
        refreshTokenRepository.save(new RefreshToken(token));
        return token;
    }

    private String scopeBuilder(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        user.getRoles().forEach(role ->  {
            stringJoiner.add("ROLE_" + role.getName());
            if (!CollectionUtils.isEmpty(role.getPermissions())) {
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            }
        });
        return stringJoiner.toString();
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) context.getAuthentication();

        String id = jwtAuthenticationToken.getToken().getClaim("userId");
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        return userMapper.toUserResponse(user);
    }

    public LoginResponse handleRefreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        if (!refreshTokenRepository.existsById(request.getToken())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        JWSVerifier verifier = new MACVerifier(jwtRefreshKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedJWT.verify(verifier) || expiration.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String userId = String.valueOf(signedJWT.getJWTClaimsSet().getClaim("userId"));
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        refreshTokenRepository.deleteById(request.getToken());
        return LoginResponse.builder()
                .accessToken(this.generateToken(user))
                .refreshToken(this.generateRefreshToken(user))
                .build();
    }
 }
