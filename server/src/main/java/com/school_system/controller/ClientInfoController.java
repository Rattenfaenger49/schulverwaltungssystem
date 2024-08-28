package com.school_system.controller;

import com.school_system.entity.school.ClientInfo;
import com.school_system.common.ResponseObject;
import com.school_system.entity.school.ClientPreferences;
import com.school_system.service.ClientInfoService;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/clientInfo")
public class ClientInfoController {

    private ClientInfoService clientInfoService;

    @PreAuthorize("@securityService.isAdmin()")
    @GetMapping
    public ResponseObject<ClientInfo> getClientInfo() {
        return  clientInfoService.getClientInfo();

    }
    //TODO notify  business owner
    @PreAuthorize("@securityService.isAdmin()")
    @PostMapping
    public ResponseObject<ClientInfo> createClientInfo(@RequestBody ClientInfo clientInfo) {
        return clientInfoService.createClientInfo(clientInfo);
    }
    @PreAuthorize("@securityService.isAdmin()")
    @PutMapping
    public ResponseObject<ClientInfo> updateClientInfo(@RequestBody ClientInfo clientInfo) {
        return clientInfoService.updateClientInfo(clientInfo);
    }
    @GetMapping("/preferences")
    public ResponseObject<ClientPreferences> getClientPreferences() {
        return  clientInfoService.getPreferences();
    }
}
