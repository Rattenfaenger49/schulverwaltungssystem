package com.school_system.service;


public interface SecurityService  {
    boolean isSameUser(Long userId);
    boolean isSameUserOrAdmin(Long userId);
    boolean isAdmin();
    boolean isSuperAdmin();
    boolean isTeacher();
    boolean isStudent();
    boolean isSupervisor();
}
