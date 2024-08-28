package com.school_system.controller;



import com.school_system.common.ResponseObject;
import com.school_system.dto.request.ResetPasswordRequest;
import com.school_system.dto.request.StudentRegistrationRequest;
import com.school_system.dto.request.TeacherRegistrationRequest;
import com.school_system.dto.request.UserRegistrationRequest;
import com.school_system.dto.response.StudentResponse;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserResponse;
import com.school_system.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URISyntaxException;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/register")
public class RegistrationController {

    private final UserService userService;

    @PostMapping("/admin")
    public ResponseObject<UserResponse> registerUser(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) throws URISyntaxException, MessagingException, IOException {
        return userService.registerUser(userRegistrationRequest);

    }
    @PostMapping("/teacher")
    public ResponseObject<TeacherResponse> registerTeacher(@RequestBody @Valid TeacherRegistrationRequest teacherRegistrationRequest) throws URISyntaxException, MessagingException, IOException {

        return userService.registerTeacher(teacherRegistrationRequest);

    }
    @PostMapping("/student")
    public ResponseObject<StudentResponse> registerStudent(@RequestBody @Valid StudentRegistrationRequest studentRegistrationRequest) throws URISyntaxException, MessagingException, IOException {
        return userService.registerStudent(studentRegistrationRequest);

    }
    // TODO implement a limit and delete those tokens used for  request a new token
    @PostMapping("/resend-confirmation-token")
    public ResponseObject<String> sendConfirmationTokenFromExpiredToken( @RequestParam String token) throws MessagingException, URISyntaxException, IOException {
            return userService.sendConfirmationToken(token);
    }

    @PreAuthorize("@securityService.isAdmin()")
    @PostMapping("/resend-confirmation-token-by-user")
    public ResponseObject<String> sendConfirmationTokenFromExpiredToken( @RequestParam Long userId) {
        return userService.sendConfirmationTokenByUserId(userId);
    }

    @PostMapping("/reset-password")
    public ResponseObject<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) throws MessagingException, URISyntaxException, IOException {

        return userService.resetPassword(resetPasswordRequest);
    }

}
