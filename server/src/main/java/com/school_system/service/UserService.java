package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.*;
import com.school_system.dto.response.*;
import com.school_system.entity.school.BankData;
import com.school_system.entity.school.FileMetadata;
import com.school_system.enums.school.ContractType;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface UserService {

    ResponseObject<UserResponse> registerUser(UserRegistrationRequest userRegistrationRequest) throws URISyntaxException, MessagingException, IOException;
    ResponseObject<TeacherResponse> registerTeacher(TeacherRegistrationRequest teacherRegistrationRequest) throws URISyntaxException, MessagingException, IOException;
    ResponseObject<StudentResponse> registerStudent(StudentRegistrationRequest studnetRegistrationRequest) throws URISyntaxException, MessagingException, IOException;

    ResponseObject<byte[]> generateDocumentation(Long studentId, ContractType contractId, String startDate, String endDate);

    ResponseObject<String> resetPassword(ResetPasswordRequest resetPasswordRequest) throws MessagingException, URISyntaxException, IOException;

    ResponseObject<List<UserFullNameResponse>> getUsersSelectList(String token);

    ResponseObject<String> sendConfirmationToken( String token) throws MessagingException, URISyntaxException, IOException;

    ResponseObject<? extends UserResponse> getProfile();

    ResponseObject<? extends UserResponse> updateProfile(UserUpdateRequest userUpdateRequest);

    ResponseObject<? extends UserResponse> deleteProfile();

    ResponseObject<? extends UserResponse> accountSperren(Long id);

    ResponseObject<BankData> getBankData(Long userId);

    ResponseObject<List<FileMetadata>> getPersonalFiles(Long userId);

    ResponseObject<String> sendConfirmationTokenByUserId(Long userId);

    ResponseObject<BankData> saveBankData(BankData bankData);
}
