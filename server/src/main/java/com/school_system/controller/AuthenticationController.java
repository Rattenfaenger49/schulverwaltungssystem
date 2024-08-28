package com.school_system.controller;

import com.school_system.common.ResponseObject;

import com.school_system.dto.request.*;
import com.school_system.service.AuthenticationService;
import com.school_system.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")

public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request, HttpServletResponse response) {
        return authenticationService.login(request, response);
    }

    @GetMapping(path = "/confirm")
    public ResponseObject<String> confirm(@RequestParam String token, HttpServletResponse response) {
        return authenticationService.confirmToken(token, response);


    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody @Valid RefreshTokenRequest request, HttpServletRequest httpRequest){
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.refreshToken(request.getRefreshToken(), httpRequest));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseObject<String> logout( HttpServletResponse response){

        return authenticationService.logout(response);


    }
    @PutMapping("/set-password")
    public ResponseObject<String> setPassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest){

        return authenticationService.setPassowrd(updatePasswordRequest);
    }



}
