package com.school_system.ScheduledTasks;

import com.school_system.config.Databases.TenantContext;
import com.school_system.config.Databases.TenantRegistry;
import com.school_system.entity.security.Teacher;
import com.school_system.entity.security.User;
import com.school_system.repository.StudentRepository;
import com.school_system.repository.TeacherRepository;
import com.school_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class deleteMarkedAsDeletedEntities {


    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron="0 0 0 * * *")
    public void runDailyDeletionOfUsers(){
        // TenantRegistry
        Set<String> tenantIds = TenantRegistry.getAllTenantIds();
        for (String tenantId : tenantIds) {

            TenantContext.setCurrentTenant(tenantId);
            LocalDateTime timestamp = LocalDateTime.now();
            List<User> usersToDelete =  userRepository.findByMarkedForDeletionTrueAndupdatedAtBefore(timestamp);

            userRepository.deleteAll(usersToDelete);

        }
    }
}

