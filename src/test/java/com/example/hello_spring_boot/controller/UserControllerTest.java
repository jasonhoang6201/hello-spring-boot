package com.example.hello_spring_boot.controller;

import com.example.hello_spring_boot.dto.request.UserCreationRequest;
import com.example.hello_spring_boot.dto.response.UserResponse;
import com.example.hello_spring_boot.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.HashSet;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;

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

        userResponse = UserResponse.builder()
                .id("test-test-test")
                .username("test")
                .firstName("test")
                .lastName("test")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_happyCase() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                        .thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("test-test-test"));
    }

    @Test
    void createUser_invalidUsername() throws Exception {
        userCreationRequest.setUsername("1");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(userCreationRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1004))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Invalid username"));
    }
}
