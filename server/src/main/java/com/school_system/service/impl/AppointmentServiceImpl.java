package com.school_system.service.impl;

import com.school_system.classes.Attachment;
import com.school_system.common.ResponseObject;
import com.school_system.dto.request.AppointmentRequest;
import com.school_system.dto.response.AppointmentResponse;
import com.school_system.dto.response.Attendees;
import com.school_system.entity.school.Appointment;
import com.school_system.entity.school.ClientInfo;
import com.school_system.entity.security.Teacher;
import com.school_system.entity.security.User;
import com.school_system.init.ProfilesLoader;
import com.school_system.mapper.Mapper;
import com.school_system.repository.AppointmentRepository;
import com.school_system.repository.UserRepository;
import com.school_system.service.AppointmentService;
import com.school_system.service.SecurityService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static com.school_system.util.EmailTemplateDataBuilder.buildAppointmentNotificationEmailVariables;


@RequiredArgsConstructor
@Service
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HttpServletResponse httpServletResponse;
    private final EmailService emailService;
    private final SecurityService securityService;
    private final ProfilesLoader profilesLoader;


    @Override
    @Cacheable(value = "appointments", key = "'list.' + #userId", condition = "false")
    public ResponseObject<List<AppointmentResponse>> getAppointments(LocalDate date, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }
        List<Appointment> appointments = appointmentRepository.findByOrganizerOrAttendeeIdForMonth(userId, date.getYear(), date.getMonthValue());

        return ResponseObject.<List<AppointmentResponse>>builder()
                .message("Appointments fetched successfully")
                .data(appointments.stream().map(Mapper::toAppointmentResponse).toList())
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .build();
    }

    @Override
    @Cacheable(value = "appointments", key = "#id", condition = "false")
    public ResponseObject<AppointmentResponse> getAppointment(Long id, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Termin nicht gefunden! Etwas ist schief gelaufen!")
        );
        Long finalUserId = userId;
        if (!Objects.equals(appointment.getOrganizer().getId(), userId) && appointment.getAttendees().stream().noneMatch(user -> Objects.equals(user.getId(), finalUserId))) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<AppointmentResponse>builder()
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .build();

        }

        return ResponseObject.<AppointmentResponse>builder()
                .message("Appointments fetched successfully")
                .data(Mapper.toAppointmentResponse(appointment))
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .build();
    }

    @Override
    @CachePut(value = "appointments", key = "#result.data.id", condition = "false")
    @CacheEvict(value = "appointments", key = "'list.'+ #userId", condition = "false")
    public ResponseObject<AppointmentResponse> createAppointment(AppointmentRequest request, Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }
        User organizer = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Benutzer nicht gefunden! Etwas ist schief gelaufen!")
        );

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(request, appointment);
        appointment.setOrganizer(organizer);

        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(UserUtils.getUsername(authentication));

        appointmentRepository.save(appointment);

        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if (clientInfo.getPreferences().getEmailNotificationsForAppointments()) {
            var logo = clientInfo.getLogoAsByteArray();
            var map = buildAppointmentNotificationEmailVariables(organizer, appointment, clientInfo);

            emailService.sendEmail(organizer.getUsername(),
                    "Termin Benachrichtigung",
                    "appointmentEmailTemplate.html",
                    map, logo, null);
        }

        return ResponseObject.<AppointmentResponse>builder()
                .message("Termin erfolgreich erstellt")
                .data(Mapper.toAppointmentResponse(appointment))
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .build();
    }

    @Override
    @Transactional
    @Caching(
            cacheable = @Cacheable(value = "appointments", key = "#id", condition = "false"),
            put = @CachePut(value = "appointments", key = "#result.data.id", condition = "false")
    )
    public ResponseObject<AppointmentResponse> updateAppointment(Long id, AppointmentRequest request, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Termin nicht gefunden! Etwas ist schief gelaufen!")
        );

        if (!Objects.equals(appointment.getOrganizer().getId(), userId)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<AppointmentResponse>builder()
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .build();
        }
        appointment.setTitle(request.getTitle());
        appointment.setContent(request.getContent());
        appointment.setContractType(request.getContractType());
        appointment.setStatus(request.getStatus());
        appointment.setStartAt(request.getStartAt());
        appointment.setEndAt(request.getEndAt());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(UserUtils.getUsername(authentication));
        appointmentRepository.save(appointment);

        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if (clientInfo.getPreferences().getEmailNotificationsForAppointments()) {
            var logo = clientInfo.getLogoAsByteArray();
            appointment.getAttendees().forEach(a -> {
                // TODO  changetermin template!
                var map = buildAppointmentNotificationEmailVariables(a, appointment, clientInfo);

                emailService.sendEmail(a.getUsername(),
                        "Termin Benachrichtigung",
                        "updateAppointmentEmailTemplate.html",
                        map, logo, null);
            });

            var map = buildAppointmentNotificationEmailVariables(appointment.getOrganizer(), appointment, clientInfo);
            emailService.sendEmail(appointment.getOrganizer().getUsername(),
                    "Termin Benachrichtigung",
                    "appointmentEmailTemplate.html",
                    map, logo, null);

        }

        return ResponseObject.<AppointmentResponse>builder()
                .message("Termin erfolgreich aktualisiert")
                .data(Mapper.toAppointmentResponse(appointment))
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .build();
    }

    @Override
    @Transactional
    @Caching(
            cacheable = @Cacheable(value = "appointments", key = "#id", condition = "false"),
            put = @CachePut(value = "appointments", key = "#result.data.id", condition = "false")
    )
    public ResponseObject<AppointmentResponse> addAttendees(Long appointmentId, Long attendeeId, Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isTeacher = securityService.isTeacher();
        boolean isAdmin = securityService.isAdmin();

        if (!isAdmin && !isTeacher) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<AppointmentResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }
        // if not Admin then the sameUser is the actual User
        // important to prevent access to other users appointment
        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }

        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() ->
                new EntityNotFoundException("Termin nicht gefunden!")
        );

        if (appointment.getAttendees().stream().anyMatch(u -> u.getId().equals(attendeeId))) {
            throw new EntityExistsException("Der Benutzer ist bereits ein Teilnehmner!");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("Benutzername wurde nicht gefunden.")
        );

        User attendee = userRepository.findById(attendeeId).orElseThrow(() ->
                new EntityNotFoundException("Benutzer nicht gefunden!"));

        if (isTeacher) {
            Teacher teacher = (Teacher) user;
            boolean a = teacher.getStudents().stream().anyMatch(s -> Objects.equals(s.getId(), attendeeId));
            if (!a) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return ResponseObject.<AppointmentResponse>builder()
                        .status(ResponseObject.ResponseStatus.FAILED)
                        .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                        .build();
            }
        }

        appointment.addAttendee(attendee);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(UserUtils.getUsername(authentication));
        appointmentRepository.save(appointment);
        // String to, String subject, String body
        String icsFile = createIcsFileContent(appointment);
        InputStreamSource calendarFile = new ByteArrayResource(icsFile.getBytes(StandardCharsets.UTF_8));
        Attachment attachment = new Attachment("termin.ics", calendarFile);
        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if (clientInfo.getPreferences().getEmailNotificationsForAppointments()) {
            var logo = clientInfo.getLogoAsByteArray();
            var map = buildAppointmentNotificationEmailVariables(attendee, appointment, clientInfo);
            emailService.sendEmail(attendee.getUsername(), "Termin Benachrichtigung",
                    "appointmentEmailTemplate.html", map, logo, List.of(attachment));
        }
        return ResponseObject.<AppointmentResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toAppointmentResponse(appointment))
                .message("Teilnehmer Erfolgreich hinzugefügt!")
                .build();
    }


    @Override
    @Transactional
    @CacheEvict(value = "appointments", key = "#id", condition = "false")
    public ResponseObject<AppointmentResponse> removeAttendees(Long id, Long attendeeId, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();

        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }
        boolean isTeacher = securityService.isTeacher();
        boolean isStudent = securityService.isStudent();

        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Termin nicht gefunden!")
        );
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("Benutzername wurde nicht gefunden.")
        );
        User attendee = userRepository.findById(attendeeId).orElseThrow(() ->
                new EntityNotFoundException("Benutzer nicht gefunden!"));
        if (isStudent && !userId.equals(attendeeId)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<AppointmentResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        } else if (isTeacher) {
            Teacher teacher = (Teacher) user;
            boolean a = teacher.getStudents().stream().anyMatch(s -> Objects.equals(s.getId(), attendeeId));
            if (!a) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return ResponseObject.<AppointmentResponse>builder()
                        .status(ResponseObject.ResponseStatus.FAILED)
                        .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                        .build();
            }
        }
        appointment.removeAttendee(attendee);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setUpdatedBy(UserUtils.getUsername(authentication));
        appointmentRepository.save(appointment);

        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if (clientInfo.getPreferences().getEmailNotificationsForAppointments()) {
            var logo = clientInfo.getLogoAsByteArray();
            var map = buildAppointmentNotificationEmailVariables(user, appointment, clientInfo);
            emailService.sendEmail(user.getUsername(),
                    "Termin Absage",
                    "appointmentCancellationEmailTemplate.html",
                    map, logo, null);
        }

        return ResponseObject.<AppointmentResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toAppointmentResponse(appointment))
                .message("Teilnehmer Erfolgreich gelöscht!")
                .build();
    }

    // TODO cache
    @Override
    @Transactional
    public ResponseObject<List<Attendees>> getAttendees(Long id, Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();

        if (userId == null || !isAdmin) {
            userId = UserUtils.getUserId(authentication);
        }
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Termin nicht gefunden!"));
        Long finalUserId = userId;
        if (!appointment.getOrganizer().getId().equals(userId) && appointment.getAttendees().stream().noneMatch(a -> Objects.equals(a.getId(), finalUserId))) {

            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<List<Attendees>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();

        }

        return ResponseObject.<List<Attendees>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toAttendees(appointment))
                .message(null)
                .build();
    }

    @Override
    public void deleteAppointment(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        long userId = UserUtils.getUserId(authentication);
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Termin nicht gefunden! Etwas ist schief gelaufen!")
        );
        if (!isAdmin && !appointment.getOrganizer().getId().equals(userId)) {
            throw new AccessDeniedException("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!");
        }
        // Lazy loading of attendees
        Hibernate.initialize(appointment.getAttendees());
        List<User> attendees = appointment.getAttendees();
        attendees.add(appointment.getOrganizer());
        appointmentRepository.deleteById(id);
        ClientInfo clientInfo = profilesLoader.getClientInfo();
        if (clientInfo.getPreferences().getEmailNotificationsForAppointments()) {

            var logo = clientInfo.getLogoAsByteArray();

            attendees.forEach(a ->
            {
                var map = buildAppointmentNotificationEmailVariables(a, appointment, clientInfo);
                emailService.sendEmail(a.getUsername(),
                        "Termin Absage",
                        "appointmentCancellationEmailTemplate.html",
                        map, logo, null);
            });
            var map = buildAppointmentNotificationEmailVariables(appointment.getOrganizer(), appointment, clientInfo);
            emailService.sendEmail(appointment.getOrganizer().getUsername(),
                    "Termin Absage",
                    "appointmentCancellationEmailTemplate.html",
                    map, logo,null);
        }

    }


    private String createIcsFileContent(Appointment appointment) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String startDate = dtf.format(appointment.getStartAt());
        String endDate = dtf.format(appointment.getEndAt());

        return "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:-//example//svs//DE\n" +
                "BEGIN:VEVENT\n" +
                "UID:" + appointment.getId() + "\n" +
                "DTSTAMP:" + startDate + "\n" +
                "ORGANIZER;CN=" + appointment.getOrganizer().getFullName() + ":MAILTO:" + appointment.getOrganizer().getUsername() + "\n" +
                "DTSTART:" + startDate + "\n" +
                "DTEND:" + endDate + "\n" +
                "DESCRIPTION:" + appointment.getContent() + "\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";
    }
}
