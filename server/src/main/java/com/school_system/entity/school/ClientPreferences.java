package com.school_system.entity.school;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientPreferences {

    @NotNull(message = "Erlauben Sie die Erstellung von Rechnungen für Lehrer muss angegeben werden.")
    private Boolean allowTeacherBillGeneration = true;

    @NotNull(message = "Erlauben Sie die Erstellung von Rechnungen für Schüler muss angegeben werden.")
    private Boolean allowStudentBillGeneration = true;

    @NotNull(message = "E-Mail-Benachrichtigungen für Termine müssen angegeben werden.")
    private Boolean emailNotificationsForAppointments = true;

    @NotNull(message = "E-Mail-Benachrichtigungen für Änderungen an Unterrichtseinheiten müssen angegeben werden.")
    private Boolean emailNotificationForLessonChanges = true;

    @NotNull(message = "E-Mail-Benachrichtigungen für Änderungen an Unterrichtseinheiten müssen angegeben werden.")
    private Boolean emailNotificationForClientInfoChanges = true;
}
