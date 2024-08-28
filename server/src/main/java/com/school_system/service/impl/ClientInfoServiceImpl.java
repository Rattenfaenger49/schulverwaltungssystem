package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.config.Databases.TenantContext;
import com.school_system.entity.school.ClientInfo;
import com.school_system.entity.school.ClientPreferences;
import com.school_system.repository.ClientInfoRepository;
import com.school_system.service.ClientInfoService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.school_system.util.EmailTemplateDataBuilder.buildNotificationForClientInfoofChangesEmailVariables;

@RequiredArgsConstructor
@Service
public class ClientInfoServiceImpl implements ClientInfoService {

    private final ClientInfoRepository clientInfoRepository;

    private final EmailService emailService;

    @Override
    public ResponseObject<ClientInfo> getClientInfo() {
        ClientInfo clientInfo = clientInfoRepository.findByTenantId(TenantContext.getCurrentTenant()).orElseThrow(
                () -> new EntityNotFoundException("Kunden-Informationen wurden nicht gefunden")
        );
        return ResponseObject.<ClientInfo>builder()
                .message("")
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(clientInfo)
                .build();
    }

    @Override
    public ResponseObject<ClientInfo> createClientInfo(ClientInfo clientInfo) {
        String tenantId = TenantContext.getCurrentTenant();
        if (clientInfoRepository.existsByTenantId(tenantId)) {
            throw new EntityNotFoundException("Kunden-Informationen sind schon Vorhanden!");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = UserUtils.getUsername(authentication);
        clientInfo.setTenantId(tenantId);
        clientInfo.setUpdatedAt(LocalDateTime.now());
        clientInfo.setUpdatedBy(UserUtils.getUsername(authentication));
        clientInfo.setCreatedAt(LocalDateTime.now());
        var variables = buildNotificationForClientInfoofChangesEmailVariables(clientInfo, username);
        if (clientInfo.getPreferences().getEmailNotificationForClientInfoChanges()) {
            emailService.sendEmail(clientInfo.getEmail(), "Benachrichtigung über Unternehmenprofile Erstellung",
                    "notificationForClientInfoChangesTemplate.html",
                    variables, clientInfo.getLogoAsByteArray(), null);
        }
        return ResponseObject.<ClientInfo>builder()
                .message("")
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(clientInfoRepository.save(clientInfo))
                .build();
    }

    @Override
    public ResponseObject<ClientInfo> updateClientInfo(ClientInfo clientInfoRequest) {
        String tenantId = TenantContext.getCurrentTenant();
        ClientInfo clientInfo = clientInfoRepository.findByTenantId(tenantId).orElseThrow(
                () -> new EntityNotFoundException("Kunden-Informationen sind nicht Vorhanden!")
        );
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = UserUtils.getUsername(authentication);
        if (clientInfo.getPreferences().getEmailNotificationForClientInfoChanges()) {
            var variables = buildNotificationForClientInfoofChangesEmailVariables(clientInfo, username);

            emailService.sendEmail(clientInfo.getEmail(), "Benachrichtigung über Unternehmenprofile Erstellung",
                    "notificationForClientInfoChangesTemplate.html",
                    variables, clientInfo.getLogoAsByteArray(), null);
        }
        clientInfoRequest.setTenantId(tenantId);
        clientInfoRequest.setUpdatedAt(LocalDateTime.now());
        clientInfoRequest.setUpdatedBy(username);
        return ResponseObject.<ClientInfo>builder()
                .message("")
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(clientInfoRepository.save(clientInfoRequest))
                .build();
    }

    // TODO create custom preferences for users
    // add the school logo
    // cache this for 1 or 2 days
    @Override
    public ResponseObject<ClientPreferences> getPreferences() {
        String tenantId = TenantContext.getCurrentTenant();
        ClientInfo clientInfo = clientInfoRepository.findByTenantId(tenantId).orElseThrow(
                () -> new EntityNotFoundException("Kunden-Informationen sind nicht Vorhanden!")
        );
        return ResponseObject.<ClientPreferences>builder()
                .data(clientInfo.getPreferences())
                .build();
    }

}
