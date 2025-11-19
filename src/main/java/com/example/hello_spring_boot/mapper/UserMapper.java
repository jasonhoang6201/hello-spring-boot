package com.example.hello_spring_boot.mapper;

import com.example.hello_spring_boot.dto.request.UserCreationRequest;
import com.example.hello_spring_boot.dto.request.UserUpdateRequest;
import com.example.hello_spring_boot.dto.response.UserResponse;
import com.example.hello_spring_boot.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserResponse toUserResponse(User user);
}

