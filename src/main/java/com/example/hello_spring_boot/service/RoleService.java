package com.example.hello_spring_boot.service;

import com.example.hello_spring_boot.dto.request.RoleRequest;
import com.example.hello_spring_boot.dto.response.RoleResponse;
import com.example.hello_spring_boot.entity.Role;
import com.example.hello_spring_boot.exception.AppException;
import com.example.hello_spring_boot.exception.ErrorCode;
import com.example.hello_spring_boot.mapper.RoleMapper;
import com.example.hello_spring_boot.repository.PermissionRepository;
import com.example.hello_spring_boot.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        Optional<Role> record = roleRepository.findById(request.getName());
        if (record.isPresent()){
            throw new AppException(ErrorCode.RECORD_EXISTED);
        }

        Role role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAllRoles(){
        return roleRepository.findAll()
                .stream().map(roleMapper::toRoleResponse)
                .toList();
    }

    public void deleteById(String name){
        roleRepository.deleteById(name);
    }
}
