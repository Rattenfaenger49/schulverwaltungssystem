package com.school_system.service.impl;

import com.school_system.service.SecurityService;
import com.school_system.util.UserUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service("securityService")
public class SecurityServiceImp implements SecurityService {

    @Override
    public boolean isSameUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = UserUtils.getUserId(authentication);
        return  id.equals(userId);
    }
    @Override
    public boolean isSameUserOrAdmin(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = UserUtils.getUserId(authentication);
        return   isAdmin() || id.equals(userId);
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roles = String.join(" ", UserUtils.getRoles(authentication));
        return  roles.contains("ADMIN");
    }

    @Override
    public boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roles = String.join(" ", UserUtils.getRoles(authentication));
        return  roles.contains("SUPERADMIN");
    }

    @Override
    public boolean isTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roles = String.join(" ", UserUtils.getRoles(authentication));
        return  roles.contains("TEACHER");
    }

    @Override
    public boolean isStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roles = String.join(" ", UserUtils.getRoles(authentication));
        return  roles.contains("STUDENT");
    }

    @Override
    public boolean isSupervisor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String roles = String.join(" ", UserUtils.getRoles(authentication));
        return  roles.contains("SUPERVISOR");
    }
}
