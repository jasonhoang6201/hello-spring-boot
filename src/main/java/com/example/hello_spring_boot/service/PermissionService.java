package com.example.hello_spring_boot.service;

import com.example.hello_spring_boot.dto.request.PermissionRequest;
import com.example.hello_spring_boot.dto.response.PermissionResponse;
import com.example.hello_spring_boot.entity.Permission;
import com.example.hello_spring_boot.mapper.PermissionMapper;
import com.example.hello_spring_boot.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAllPermissions(){
        return permissionRepository.findAll()
                .stream().map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public void deleteById(String name){
        permissionRepository.deleteById(name);
    }
}
