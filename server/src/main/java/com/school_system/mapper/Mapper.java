package com.school_system.mapper;

import com.school_system.dto.CustomUserDetails;
import com.school_system.dto.response.*;
import com.school_system.entity.school.*;
import com.school_system.entity.school.Views.LessonsTable;
import com.school_system.entity.security.*;
import com.school_system.enums.authentication.RoleName;
import com.school_system.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Mapper {

    public static CustomUserDetails toCustomUserDetails(User user){

        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(user, customUserDetails, "roles");

        customUserDetails.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        Set<Permission> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toSet());
        customUserDetails.setPermissions(permissions);

        return customUserDetails;
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);

        return userResponse;
    }

    public static InvoiceResponse toInvoiceResponse(Invoice invoice) {
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        BeanUtils.copyProperties(invoice, invoiceResponse, "teacher");
        BasePerson basePerson = new BasePerson(
                invoice.getUser().getId(),
                invoice.getUser().getFirstName(),
                invoice.getUser().getLastName(),
                invoice.getUser().getUsername()
                );
        invoiceResponse.setTeacher(basePerson);
        invoiceResponse.setFileId(invoice.getFile().getId());
        invoiceResponse.setSigned(invoice.getSignature() == null);
        return invoiceResponse;
    }
    public static TeacherResponse toTeacherResponse(Teacher teacher) {
        TeacherResponse teacherResponse = new TeacherResponse();
        BeanUtils.copyProperties(teacher, teacherResponse, "students");

        List<Student> students = teacher.getStudents();

        List<StudentResponse> studentsResponse = students.stream().map(Mapper::toStudentResponse).toList();
        teacherResponse.setStudents(studentsResponse);

        return teacherResponse;
    }
    public static UserTable toUserTable(User user) {
        UserTable userTable = new UserTable();
        BeanUtils.copyProperties(user, userTable);
        return userTable;
    }

    public static StudentResponse toStudentResponse(Student student) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = UserUtils.hasRole(authentication, RoleName.ADMIN);

        StudentResponse studentResponse = new StudentResponse();
        BeanUtils.copyProperties(student, studentResponse, "contracts", "teachers");
        List<ContractResponse> contractResponse = student.getContracts().stream().map(Mapper::toContractResponse).toList();
        List<BasePerson> teacherResponses = student.getTeachers().stream().map(t ->{
            BasePerson basePerson = new BasePerson();
            BeanUtils.copyProperties(t, basePerson);
            return basePerson;
        }).toList();
        studentResponse.setContracts(contractResponse);
        studentResponse.setTeachers(teacherResponses);

        if(!isAdmin){
            studentResponse.getContracts().parallelStream().forEach(contract -> {
                contract.setContact(null);
                contract.getModuls().parallelStream().forEach( module -> {
                    module.setGroupLessonCost(BigDecimal.ZERO);
                    module.setSingleLessonCost(BigDecimal.ZERO);
                });
            });
        }
        return studentResponse;
    }

    public static LessonResponse toLessonResponse(Lesson lesson) {
        LessonResponse lessonResponse = new LessonResponse();
        BeanUtils.copyProperties(lesson, lessonResponse, "teacher", "students");
        lessonResponse.setTeacher(toUserFullNameResponse(lesson.getTeacher()));
        lessonResponse.setStudentsLesson(toStudentLessonResponse(lesson.getStudentLessons()));
        lessonResponse.setIsSigned(
                lesson.getSignature().isEmpty() ? "Nein" : "Ja"
        );
        return lessonResponse;
    }


    private static List<StudentLessonResponse> toStudentLessonResponse(List<StudentLesson> studentsLesson) {

        return studentsLesson.stream().map( sl ->{
            StudentLessonResponse studentLessonResponse = new StudentLessonResponse();
            studentLessonResponse.setLessonId(sl.getId().getLessonId());
            studentLessonResponse.setStudent(toUserFullNameResponse(sl.getStudent()));
            studentLessonResponse.setModul(toModulResponse(sl.getModul()));
            return studentLessonResponse;
        }).toList();

    }

    public static ContractResponse toContractResponse(Contract contract) {
        ContractResponse contractResponse = new ContractResponse();
        BeanUtils.copyProperties(contract, contractResponse, "student", "moduls");
        contractResponse.setModuls(contract.getModuls().stream().map(Mapper::toModulResponse).toList());
        contractResponse.setStudent(Mapper.toContractStudentResponse(contract.getStudent()));


        return contractResponse;
    }

    private static ContractStudentResponse toContractStudentResponse(Student student) {
        ContractStudentResponse contractStudentResponse = new ContractStudentResponse();
        BeanUtils.copyProperties(student, contractStudentResponse);

        return contractStudentResponse;
    }
    public static ModulResponse toModulResponse(Modul modul){
        ModulResponse modulResponse = new ModulResponse();
        BeanUtils.copyProperties(modul, modulResponse, "contract");
        // set Contract Id in Modul
        // FIXME optimiz th moduls Response
        BaseEntityId contract = new BaseEntityId(modul.getContract().getId());

        modulResponse.setContract(contract);
        return modulResponse;
    }

    public static UserFullNameResponse toUserFullNameResponse(User user) {
        if(user == null)
            return null;
        UserFullNameResponse userResponse = new UserFullNameResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }
    public static InstitutionResponse toInstituionResponse(Institution institution) {
        InstitutionResponse institutionResponse = new InstitutionResponse();
        BeanUtils.copyProperties(institution, institutionResponse, "contacts");
        institutionResponse.setContacts(institution.getContacts().stream().map(Mapper::toContactResponse).toList());
        return institutionResponse;
    }

    private static ContactResponse toContactResponse(Contact contact) {
        ContactResponse contactResponse = new ContactResponse();
        BeanUtils.copyProperties(contact, contactResponse, "institution");
        return contactResponse;
    }


    public static AppointmentResponse toAppointmentResponse(Appointment appointment) {
        AppointmentResponse appointmentResponse = new AppointmentResponse();
        BeanUtils.copyProperties(appointment, appointmentResponse, "organizer", "attendees");
        appointmentResponse.setOrganizer(toUserFullNameResponse(appointment.getOrganizer()));
        appointmentResponse.setAttendees(appointment.getAttendees().stream().map(Mapper::toUserFullNameResponse).toList());
        return appointmentResponse;
    }
    public static LessonsTable toLessonsTable(Lesson lesson) {
        Long teacherId = lesson.getTeacher() != null ? lesson.getTeacher().getId() : null;

        return LessonsTable.builder()
                .teacherId(teacherId)
                .modulType(lesson.getModulType())
                .teacher( teacherId != null ? lesson.getTeacher().getFullName() : "Nicht zugewiesen")
                .students(lesson.getStudentLessons().stream().map(sl -> sl.getStudent().getFullName()).collect(Collectors.joining(", ")))
                .isSigned(lesson.getSignature().isEmpty() ? "Nein" : "Ja" )
                .id(lesson.getId())
                .startAt(lesson.getStartAt())
                .build();
    }

    public static List<Attendees> toAttendees(Appointment appointment) {

        return appointment.getAttendees().stream()
                .map(user -> {
                    Attendees attendee = new Attendees();
                    BeanUtils.copyProperties(user, attendee);
                    return attendee;
                })
                .collect(Collectors.toList());
    }
    public static LessonVersion toLessonVersion(Lesson lesson){

        String studentsIds = lesson.getStudentLessons().parallelStream().map(s -> s.getStudent().getId().toString()).collect(Collectors.joining(";"));
        String signaturesIds = lesson.getSignature().parallelStream().map(s -> s.getId().toString()).collect(Collectors.joining(";"));


        LessonVersion lessonVersion = new LessonVersion();
        BeanUtils.copyProperties(lesson, lessonVersion, "id");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        lessonVersion.setStudentsIds(studentsIds);
        lessonVersion.setSignaturesIds(signaturesIds);
        lessonVersion.setTeacherId(lesson.getTeacher().getId());
        lessonVersion.setLessonId(lesson.getId());
        lessonVersion.setCreatedAt(LocalDateTime.now());
        lessonVersion.setCreatedBy(UserUtils.getUsername(authentication));
        return lessonVersion;
    }
}
