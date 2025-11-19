package com.example.hello_spring_boot.service;

import com.example.hello_spring_boot.dto.request.UserCreationRequest;
import com.example.hello_spring_boot.entity.User;
import com.example.hello_spring_boot.exception.AppException;
import com.example.hello_spring_boot.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.HashSet;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;
    private UserCreationRequest userCreationRequest;
    private User user;

    @BeforeEach
    public void init() {
        LocalDate dob = LocalDate.of(2001, 10, 10);

        userCreationRequest = UserCreationRequest.builder()
                .username("test")
                .firstName("test firstname")
                .lastName("test lastname")
                .password("demo@123")
                .dob(dob)
                .roles(new HashSet<>())
                .build();


        user = User.builder()
                .username("test")
                .firstName("test firstname")
                .lastName("test lastname")
                .dob(dob)
                .roles(new HashSet<>())
                .build();
    }

    @Test
    void createUser_happyCase() throws Exception {
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        var res = userService.createUser(userCreationRequest);

        Assertions.assertThat(res.getId()).isEqualTo(user.getId());
        Assertions.assertThat(res.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    void createUser_existedUser() throws Exception {
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(true);
        var e = org.junit.jupiter.api.Assertions.assertThrows(AppException.class, () -> userService.createUser(userCreationRequest));
        Assertions.assertThat(e.getErrorCode().getCode()).isEqualTo(1002);
        Assertions.assertThat(e.getErrorCode().getMessage()).isEqualTo("User existed");
    }
}
