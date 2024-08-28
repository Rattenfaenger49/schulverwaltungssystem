package com.school_system.service.impl;

import com.school_system.entity.security.Role;
import com.school_system.enums.authentication.RoleName;
import com.school_system.enums.authentication.UserType;
import com.school_system.repository.RoleRepository;
import com.school_system.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByUserType(UserType userType) {
        RoleName roleName = switch (userType) {
            case STUDENT -> RoleName.STUDENT;
            case SUPERVISOR -> RoleName.SUPERVISOR;
            case TEACHER -> RoleName.TEACHER;
            case ADMIN -> RoleName.ADMIN;
        };

        return roleRepository.findByName(roleName).orElseThrow(() -> new
                EntityNotFoundException("Role not found"));
    }
}
