package com.school_system.service;

import com.school_system.entity.security.Role;
import com.school_system.enums.authentication.UserType;

public interface RoleService {

    Role getRoleByUserType(UserType userType);
}
