package com.example.hello_spring_boot.controller;

import com.example.hello_spring_boot.dto.request.ApiResponse;
import com.example.hello_spring_boot.dto.request.RoleRequest;
import com.example.hello_spring_boot.dto.response.RoleResponse;
import com.example.hello_spring_boot.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/roles")
public class RoleController {
    RoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> addRole(@RequestBody @Valid RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRole(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteRole(@PathVariable String id){
        roleService.deleteById(id);
        return ApiResponse.builder()
                .result(true)
                .build();
    }
}
