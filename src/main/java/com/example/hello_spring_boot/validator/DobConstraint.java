package com.example.hello_spring_boot.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {DobValidator.class}
)
public @interface DobConstraint {
    String message() default "Dob invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min();
}
