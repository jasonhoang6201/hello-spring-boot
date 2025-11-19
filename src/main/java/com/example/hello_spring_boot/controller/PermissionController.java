package com.example.hello_spring_boot.controller;

import com.example.hello_spring_boot.dto.request.ApiResponse;
import com.example.hello_spring_boot.dto.request.PermissionRequest;
import com.example.hello_spring_boot.dto.response.PermissionResponse;
import com.example.hello_spring_boot.service.PermissionService;
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
@RequestMapping("/permissions")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> addPermission(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAllPermission(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deletePermission(@PathVariable String id){
        permissionService.deleteById(id);
        return ApiResponse.builder()
                .result(true)
                .build();
    }
}
