package com.example.hello_spring_boot.dto.request;

import com.example.hello_spring_boot.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 1, max = 100, message = "INVALID_USERNAME")
    String username;
    @Size(min = 8, max = 100, message = "INVALID_PASSWORD")
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min = 10, message = "DOB_INVALID")
    LocalDate dob;

    Set<String> roles;
}
