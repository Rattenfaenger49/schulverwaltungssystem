package com.school_system.util;

import com.school_system.entity.school.Appointment;
import com.school_system.entity.security.User;
import com.school_system.entity.school.ClientInfo;

import java.util.Map;

public class EmailTemplateDataBuilder {
    public static Map<String, String> buildRegistragionEmailVariables(User user, String confirmationLink, ClientInfo clientInfo){
        return Map.of(
                "LINK", confirmationLink,
                "COMPANY_EMAIL", clientInfo.getSupportEmail(),
                "COMPANY_NAME", clientInfo.getCompanyName(),
                "RECIPIENT", user.getFullName()
        );
    }
    public static Map<String, String> buildAppointmentNotificationEmailVariables(User user, Appointment appointment, ClientInfo clientInfo){
        return Map.of(
                "RECIPIENT", user.getFullName(),
                "DATUM", appointment.getStartAt().toLocalDate().toString(),
                "TIME_BEGIN", appointment.getStartAt().toLocalTime().toString(),
                "TIME_END", appointment.getEndAt().toLocalTime().toString(),
                "ORGANISATOR", appointment.getOrganizer().getFullName(),
                "COMPANY_EMAIL", clientInfo.getEmail(),
                "COMPANY_NAME", clientInfo.getCompanyName(),
                "DESCRIPTION", appointment.getContent() != null ? appointment.getContent() : "keine"
        );
    }

    public static Map<String, String> buildResetPasswordEmailVariables(User user, String link, ClientInfo clientInfo){
        return Map.of(
                "RECIPIENT", user.getFullName(),
                "COMPANY_EMAIL", clientInfo.getSupportEmail(),
                "LINK", link,
                "COMPANY_NAME", clientInfo.getCompanyName()
        );

    }

    public static Map<String, String> buildNotificationForClientInfoofChangesEmailVariables(ClientInfo clientInfo, String user){
        return Map.of(
                "RECIPIENT", clientInfo.getFullName(),
                "COMPANY_EMAIL", clientInfo.getSupportEmail(),
                "COMPANY_NAME", clientInfo.getCompanyName(),
                "MODIFIED_BY", user
        );
    }
}
