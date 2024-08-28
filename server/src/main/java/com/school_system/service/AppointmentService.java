package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.AppointmentRequest;
import com.school_system.dto.response.AppointmentResponse;
import com.school_system.dto.response.Attendees;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    ResponseObject<List<AppointmentResponse>> getAppointments( LocalDate date, Long userId);

    ResponseObject<AppointmentResponse> getAppointment(Long id, Long userId);

    ResponseObject<AppointmentResponse> createAppointment(AppointmentRequest request, Long userId);

    void deleteAppointment(Long id);

    ResponseObject<AppointmentResponse> updateAppointment(Long id, AppointmentRequest request, Long userId);

    ResponseObject<AppointmentResponse> addAttendees(Long id ,Long attendeeId, Long userId) throws MessagingException, IOException;

    ResponseObject<AppointmentResponse> removeAttendees(Long id, Long attendeeId, Long userId);

    ResponseObject<List<Attendees>> getAttendees(Long id, Long userId);
}
