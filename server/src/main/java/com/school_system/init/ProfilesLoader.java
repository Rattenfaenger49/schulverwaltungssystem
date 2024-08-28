package com.school_system.init;


import com.school_system.entity.school.ClientInfo;
import com.school_system.config.Databases.TenantContext;
import com.school_system.repository.ClientInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;



@AllArgsConstructor
@Component
@Slf4j
public class ProfilesLoader {

    private final  ClientInfoRepository clientInfoRepository;


    public  ClientInfo getClientInfo() {
        return clientInfoRepository.findByTenantId(TenantContext.getCurrentTenant()).orElseThrow(
                () -> new EntityNotFoundException("Unternehmenprofil wurde nicht angelegt! Bitte erstellen Sie einen profil an!")
        );

    }

}

