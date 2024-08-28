package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.AppointmentRequest;
import com.school_system.dto.response.AppointmentResponse;
import com.school_system.dto.response.Attendees;
import com.school_system.service.AppointmentService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseObject<List<AppointmentResponse>> getAppointments(@RequestParam LocalDate date,
                                                                     @RequestParam(required = false) Long userId) {
        return appointmentService.getAppointments(date, userId);

    }
    @GetMapping("/{id}")
    public ResponseObject<AppointmentResponse> getAppointment(@PathVariable Long id,
                                                              @RequestParam(required = false) Long userId) {
        return appointmentService.getAppointment(id, userId);

    }
    @GetMapping("/{id}/attendees")
    public ResponseObject<List<Attendees>> getAttendees(@PathVariable Long id,
                                                        @RequestParam(required = false) Long userId) {

        return appointmentService.getAttendees(id, userId);
    }
    @PostMapping
    public ResponseObject<AppointmentResponse> createAppointments(@RequestBody @Valid AppointmentRequest request,
                                                                  @RequestParam(required = false) Long userId) {

        return appointmentService.createAppointment(request, userId);

    }
    @PostMapping("/{id}/attendees")
    public ResponseObject<AppointmentResponse> addAttendees(@PathVariable Long id,
                                                            @RequestParam(required = false) Long userId,
                                                            @RequestBody Map<String, Object> requestBody) throws MessagingException, IOException {
        Integer a = (Integer) requestBody.get("id");
        Long attendeeId = a.longValue(); ;
        return appointmentService.addAttendees(id, attendeeId, userId);

    }
    @DeleteMapping("/{id}/attendees/{attendeeId}")
    public ResponseObject<AppointmentResponse> removeAttendees(@PathVariable Long id,
                                                               @RequestParam(required = false) Long userId,
                                                               @PathVariable Long attendeeId) {

        return appointmentService.removeAttendees(id, attendeeId, userId);

    }
    @PutMapping("/{id}")
    public ResponseObject<AppointmentResponse> updateAppointment(@PathVariable Long id,
                                                                 @RequestParam(required = false) Long userId,
                                                                 @RequestBody @Valid AppointmentRequest request) {
        return appointmentService.updateAppointment(id, request, userId);

    }
    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);

    }
}
