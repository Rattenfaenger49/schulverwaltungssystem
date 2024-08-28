package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.CustomUserDetails;
import com.school_system.dto.request.ResetPasswordRequest;
import com.school_system.dto.request.UpdatePasswordRequest;
import com.school_system.dto.response.AuthenticationResponse;
import com.school_system.dto.request.AuthenticationRequest;
import com.school_system.dto.request.RefreshTokenRequest;
import com.school_system.entity.security.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {

    ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request, HttpServletResponse response);

    AuthenticationResponse buildAuthenticationResponse(CustomUserDetails user, HttpServletResponse response);
    ResponseObject<String> confirmToken(String token, HttpServletResponse response);
    AuthenticationResponse refreshToken(String refreshTokenValue, HttpServletRequest httpRequest);


    ResponseObject<String> logout(HttpServletResponse response);

    ResponseObject<String> setPassowrd(UpdatePasswordRequest updatePasswordRequest);

}
