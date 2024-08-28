package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.common.ValidationResponse;
import com.school_system.dto.request.LessonRequest;
import com.school_system.dto.request.StudentUpdateRequest;
import com.school_system.dto.response.StudentResponse;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.entity.school.Contract;
import com.school_system.entity.school.Modul;
import com.school_system.entity.school.Parent;
import com.school_system.entity.school.StudentLesson;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.authentication.RoleName;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import com.school_system.mapper.Mapper;
import com.school_system.repository.ParentRepository;
import com.school_system.repository.StudentRepository;
import com.school_system.repository.TeacherRepository;
import com.school_system.service.AddressService;
import com.school_system.service.StudentService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final AddressService addressService;
    private final HttpServletResponse httpServletResponse;
    private final SecurityServiceImp securityService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              TeacherRepository teacherRepository, ParentRepository parentRepository,
                              HttpServletResponse httpServletResponse,
                              @Lazy AddressService addressService, SecurityServiceImp securityService) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
        this.addressService = addressService;
        this.httpServletResponse = httpServletResponse;
        this.securityService = securityService;
    }


    @Override
    public ResponseObject<Page<StudentResponse>> getAllStudents(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();

        if (!isAdmin && !isTeacher) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<Page<StudentResponse>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }
        Page<Student> studentsPage = isAdmin ?
                studentRepository.findAll(pageable) :
                studentRepository.findAssociatedStudentsByTeacherId(UserUtils.getUsername(authentication), pageable);

        return ResponseObject.<Page<StudentResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(studentsPage.map(Mapper::toStudentResponse))
                .build();

    }

    // FIXME create a view or simplyfy the sql query
    @Override
    public ResponseObject<Page<StudentResponse>> getStudentWithQuery(Pageable pageable, String query, String filter) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();


        if (!isAdmin && !isTeacher) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<Page<StudentResponse>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }
        Page<Student> studentsPage;
        if (isAdmin) {
            studentsPage = studentRepository.searchStudentsFilter(query, filter, pageable);
        } else {
            Teacher teacher = new Teacher();
            teacher.setId(UserUtils.getUserId(authentication));
            studentsPage = studentRepository.searchAssociatedStudentsByTeacherId(query, filter, teacher, pageable);
        }


        return ResponseObject.<Page<StudentResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(studentsPage.map(Mapper::toStudentResponse))
                .build();

    }


    @Override
    public ResponseObject<Page<StudentResponse>> getAssociatedStudentsByTeacherId(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Page<Student> studentListResponse = studentRepository.findAssociatedStudentsByTeacherId(UserUtils.getUsername(authentication), pageable);
        return ResponseObject.<Page<StudentResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(studentListResponse.map(Mapper::toStudentResponse))
                .build();
    }

    @Override
    public ValidationResponse checkIfStudentCanTakeLesson(Long studentId, LessonRequest lessonRequest) {

        // Get Student-Info
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            return new ValidationResponse(false, "Schüler/in nicht gefunden!", null);
        }
        if(student.isMarkedForDeletion()){
            return new ValidationResponse(false, "Schüler/in ist zur Löschung markiert!", null);
        }
        // init some date-info
        LocalDate lessonStartDate = lessonRequest.getStartAt().toLocalDate();

        // individual contract has no restrictions
        if (lessonRequest.getContractType() == ContractType.INDIVIDUALLY) {
            return new ValidationResponse(true, "individual Verträge haben keine Einschränkungen!", null);
        }
        // Check if student has valid contract with the same modul
        Optional<Contract> studentContractOpt = student.getContracts().stream().filter(contract ->
                        // get active contract oder beendet
                        // contract has to have the same type
                        lessonRequest.getContractType() == contract.getContractType() &&
                                // lesson has to start after contract beginn and end bevor contract end
                                (lessonStartDate.isAfter(contract.getStartAt()) || lessonStartDate.isEqual(contract.getStartAt())) &&
                                (lessonStartDate.isBefore(contract.getEndAt()) || lessonStartDate.isEqual(contract.getStartAt())) &&
                                // contract is active of terminated for update old lesson thire contracts is terminated
                                (contract.getStatus() == ContractStatus.ACTIVE || contract.getStatus() == ContractStatus.TERMINATED)
                                // contract has the required module
                                && contract.getModuls().stream().anyMatch(modul ->
                                modul.getModulType().equals(lessonRequest.getModulType()))
                // Find first will make the job, because there is just 1 contract of type x
        ).findFirst();
        if (studentContractOpt.isEmpty()) {
            return new ValidationResponse(false, String.format("Schüler/in %s hat keinen gültigen Vertrag mit passendem Modul.",
                    student.getFullName()), null);
        }
        // variable to check if single or Group is allowed
        Contract usedStudentContract = studentContractOpt.get();
        boolean isGroup = lessonRequest.getStudents().size() > 1;
        Modul usedStudentModul = usedStudentContract.getModuls().stream().filter(m -> m.getModulType().equals(lessonRequest.getModulType())).findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Module not found although check is passed!"));
        // if group then group has to be  allowed
        // else then single  has to be allowed
        if (!((isGroup && usedStudentModul.isGroupLessonAllowed())
                || (!isGroup && usedStudentModul.isSingleLessonAllowed()))) {
            return new ValidationResponse(false, String.format("Der Schüler %s kann keine %s nehmen", student.getFullName(), isGroup ?
                    "Gruppenunterricht" : "Einzelunterricht"), null);
        }

        // Get total given units of lessons this week
        double totalGivenUnits = getTotalGivenUnits(lessonRequest, student, usedStudentModul, usedStudentContract);


        double allowedUnits = usedStudentModul.getUnits();

        boolean unitsValid = (allowedUnits - totalGivenUnits) >= lessonRequest.getUnits();
        if (!unitsValid) {
            return new ValidationResponse(false, String.format("Schüler/in: %s hat nur noch %f Unterrichtseinheiten.",
                    student.getFullName(), (allowedUnits - totalGivenUnits)), null);
        }
        return new ValidationResponse(true, "Lesson is valid", usedStudentModul);


    }

    private double getTotalGivenUnits(LessonRequest lessonRequest, Student student, Modul usedStudentModul, Contract usedStudentContract) {

        // Check if student has lesson in the same week
        List<StudentLesson> givenLessons = student.getStudentLessons().stream().filter(studentLesson ->
                studentLesson.getModul().equals(usedStudentModul)).toList();

        if (lessonRequest.getContractType() == ContractType.WEEK) {
            LocalDateTime startOfWeek = lessonRequest.getStartAt()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfWeek = lessonRequest.getStartAt()
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).withHour(23).withMinute(59).withSecond(59);
            // get just lesson with same ContractType
            givenLessons = givenLessons.stream().filter(studentLesson ->
                    (studentLesson.getLesson().getStartAt().isAfter(startOfWeek) || studentLesson.getLesson().getStartAt().isEqual(startOfWeek)) &&
                            (studentLesson.getLesson().getStartAt().isBefore(endOfWeek) || studentLesson.getLesson().getStartAt().isEqual(endOfWeek))
            ).toList();
        } else if (lessonRequest.getContractType() == ContractType.MONTH) {

            LocalDateTime startOfMonth = lessonRequest.getStartAt()
                    .with(TemporalAdjusters.firstDayOfMonth());
            LocalDateTime endOfMonth = lessonRequest.getStartAt()
                    .with(TemporalAdjusters.lastDayOfMonth());
            // get just lesson with same ContractType
            givenLessons = givenLessons.stream().filter(studentLesson ->
                    (studentLesson.getLesson().getStartAt().isAfter(startOfMonth) || studentLesson.getLesson().getStartAt().isEqual(startOfMonth)) &&
                            (studentLesson.getLesson().getStartAt().isBefore(endOfMonth) || studentLesson.getLesson().getStartAt().isEqual(endOfMonth))
            ).toList();

        } else {
            LocalDate lessonStartDate = lessonRequest.getStartAt().toLocalDate();
            // get just lesson with same ContractType
            givenLessons = givenLessons.stream().filter(studentLesson ->
                    (lessonStartDate.isAfter(usedStudentContract.getStartAt()) || lessonStartDate.isEqual(usedStudentContract.getStartAt())) &&
                            (lessonStartDate.isBefore(usedStudentContract.getEndAt()) || lessonStartDate.isEqual(usedStudentContract.getEndAt()))
            ).toList();

        }
        return givenLessons.stream()
                .filter(gl -> !gl.getLesson().getId().equals(lessonRequest.getId()))
                .mapToDouble(gL -> gL.getLesson().getUnits()) // Assuming Lesson class has a getUnits() method
                .sum();
    }


    @Override
    @CacheEvict(value = "students", key = "#id", condition = "false")
    public ResponseObject<StudentResponse> deleteStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Schüler/in nicht gefunden!"));
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = UserUtils.getUsername(authentication);
            student.deleteProfile();
            student.setUpdatedAt(LocalDateTime.now());
            student.setUpdatedBy(username);
            studentRepository.save(student);


            return ResponseObject.<StudentResponse>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Schüler erfolgreich gelöscht!")
                    .data(Mapper.toStudentResponse(student))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Der Schüler konnte nicht gelöscht werden!");
        }
    }

    @Override
    @CachePut(value = "students", key = "#id", condition = "false")
    public ResponseObject<StudentResponse> undoDeleteStudent(Long id) {

        Student student = studentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Schüler mit der ID = %d wurde nicht gefunden!", id)));
        student.undoDeleteProfile();
        student.setUpdatedAt(LocalDateTime.now());
        student.setUpdatedBy(UserUtils.getUsername(SecurityContextHolder.getContext().getAuthentication()));
        studentRepository.save(student);
        return ResponseObject.<StudentResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Lehrer erfolgreich wiederhergestellt!")
                .data(Mapper.toStudentResponse(student))
                .build();

    }


    @Transactional
    @Override
    @CachePut(value = "students", key = "#result.data.id", condition = "false")
    public ResponseObject<StudentResponse> updateStudent(StudentUpdateRequest studentRequest) {
        Student student = studentRepository.findById(studentRequest.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Schüler %d nicht gefunden!", studentRequest.getId())));
        if (student.isMarkedForDeletion()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<StudentResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .data(Mapper.toStudentResponse(student))
                    .message("Schüler wurde zum Löschen markiert und kann nicht aktualisiert werden!")
                    .build();
        }

        BeanUtils.copyProperties(studentRequest, student, "address", "contracts", "parent");
        if (!Objects.equals(student.getAddress(), studentRequest.getAddress())) {
            student.setAddress(addressService.getExistingOrSaveNewAddress(studentRequest.getAddress()));
        }
        if (studentRequest.getParent() != null) {
            Parent parent = Parent.builder()
                    .id(studentRequest.getParent().getId())
                    .gender(studentRequest.getParent().getGender())
                    .firstName(studentRequest.getParent().getFirstName())
                    .lastName(studentRequest.getParent().getLastName())
                    .phoneNumber(studentRequest.getParent().getPhoneNumber())
                    .email(studentRequest.getParent().getEmail())
                    .build();
            student.setParent(parent);
            parentRepository.save(parent);
        }


        studentRepository.save(student);
        return ResponseObject.<StudentResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toStudentResponse(student))
                .message("Schüler erfolgreich aktualisiert!")
                .build();

    }

    @Override
    @Cacheable(value = "students", key = "#id", condition = "false")
    public ResponseObject<StudentResponse> getStudentById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Schüler mit id=%d nicht gefunden!", id))
        );
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();
        Long userId = UserUtils.getUserId(authentication);
        if (!isAdmin && !isTeacher && !userId.equals(id)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<StudentResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }

        return ResponseObject.<StudentResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toStudentResponse(student))
                .build();
    }

    @Override
    @Cacheable(value = "students", key = "'names'", condition = "false")
    public ResponseObject<List<UserFullNameResponse>> getStudentsFullname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();

        if (!isAdmin && !isTeacher) {
            return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Sie haben keine Berechtigung!")
                    .build();
        }

        List<UserFullNameResponse> userList = isAdmin ?
                studentRepository.findAllWithFullname() :
                teacherRepository.findStudentsWithFullname(UserUtils.getUserId(authentication));

        return ResponseObject.<List<UserFullNameResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(userList)
                .build();
    }
}
/**
 * @Override public ResponseObject<Student> getStudentProfileById(Long id) {
 * Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
 * Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
 * Optional<Student> student = studentRepository.findById(id);
 * if (student.isPresent()) {
 * boolean isAdmin = roles.stream()
 * .anyMatch(authority -> authority.getAuthority().equals("admin"));
 * boolean isTeacher = roles.stream()
 * .anyMatch(authority -> authority.getAuthority().equals("teacher"));
 * if (isAdmin) {
 * return ResponseObject.<Student>builder()
 * .status(ResponseObject.ResponseStatus.SUCCESSFUL)
 * .data(student.get())
 * .build();
 * } else if(isTeacher) {
 * <p>
 * boolean isAssigned = teacherRepository.isStudentAssignedToTeacher(authentication.getName(), id);
 * if (isAssigned) {
 * return ResponseObject.<Student>builder()
 * .status(ResponseObject.ResponseStatus.SUCCESSFUL)
 * .data(student.get())
 * .build();
 * }else {
 * <p>
 * return ResponseObject.<Student>builder()
 * .status(ResponseObject.ResponseStatus.FAILED)
 * .data(null)
 * .message("you don't have permission to get this student data!")
 * .build();
 * }
 * }
 * }
 * return ResponseObject.<Student>builder()
 * .status(ResponseObject.ResponseStatus.FAILED)
 * .data(null)
 * .message("Student not found!")
 * .build();
 * }
 **/
