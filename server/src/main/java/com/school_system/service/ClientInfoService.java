package com.school_system.service;

import com.school_system.entity.school.ClientInfo;
import com.school_system.common.ResponseObject;
import com.school_system.entity.school.ClientPreferences;

public interface ClientInfoService {
    ResponseObject<ClientInfo> getClientInfo();
    ResponseObject<ClientInfo> createClientInfo(ClientInfo clientInfo);
    ResponseObject<ClientInfo> updateClientInfo(ClientInfo clientInfoRequest);
    ResponseObject<ClientPreferences> getPreferences();
}
