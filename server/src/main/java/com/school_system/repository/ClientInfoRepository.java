package com.school_system.repository;

import com.school_system.entity.school.ClientInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClientInfoRepository extends MongoRepository<ClientInfo, String> {
    Optional<ClientInfo> findByTenantId(String tenantId);

    boolean existsByTenantId(String currentTenant);
}