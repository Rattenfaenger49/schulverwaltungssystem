package com.school_system.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@PreAuthorize("@securityService.isAdmin()")
@RequestMapping

public class Homecontroller {


    @RequestMapping("/")
    public String home(){
        return "Welcome to the School System";
    }
}
