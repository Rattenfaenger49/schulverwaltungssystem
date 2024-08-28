package com.school_system.controller;

import com.school_system.common.ResponseObject;

import com.school_system.dto.request.UserUpdateRequest;
import com.school_system.dto.response.UserResponse;
import com.school_system.entity.school.BankData;
import com.school_system.entity.school.FileMetadata;
import com.school_system.enums.school.ContractType;
import com.school_system.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    @GetMapping("/profile")
    public ResponseObject<? extends UserResponse> getProfile(){
        return this.userService.getProfile();
    }

    @GetMapping("/{userId}/document")
    @PreAuthorize("@securityService.isSameUserOrAdmin(#userId)")
    public ResponseObject<byte[]> generateDocumentation(@PathVariable @NotNull Long userId,
                                                        @RequestParam String start,
                                                        @RequestParam String end,
                                                        @RequestParam  String contractType
    ){
        ContractType contract = ContractType.fromValue(contractType.toLowerCase());

        return userService.generateDocumentation(userId, contract, start, end);


    }

    @GetMapping("/bankdata/{userId}")
    @PreAuthorize("@securityService.isSameUserOrAdmin(#userId)")
    public ResponseObject<BankData> getBankData(@PathVariable @NotNull Long userId){
        return userService.getBankData(userId);
    }

    @PostMapping("/bankdata")
    @PreAuthorize("@securityService.isSameUserOrAdmin(#bankData.user.id)")
    public ResponseObject<BankData> saveBankData(@RequestBody @Valid BankData bankData){
        return userService.saveBankData(bankData);
    }

    @GetMapping("/files")
    @PreAuthorize("@securityService.isSameUserOrAdmin(#userId)")
    public ResponseObject<List<FileMetadata>> getpersonalFiles(@RequestParam Long userId){
        return userService.getPersonalFiles(userId);


    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<? extends UserResponse> accountSperren(@PathVariable Long id){
        return this.userService.accountSperren(id);
    }
    @PutMapping("/profile")
    public ResponseObject<? extends UserResponse> updateProfile(@RequestBody UserUpdateRequest userUpdateRequest){
        return this.userService.updateProfile(userUpdateRequest);
    }
    @DeleteMapping("/profile")
    public ResponseObject<? extends UserResponse> deleteProfile(){
        return this.userService.deleteProfile();
    }

}
