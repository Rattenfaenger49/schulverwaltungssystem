package com.school_system.service.impl;

import com.school_system.common.LessonValidationResponse;
import com.school_system.common.ResponseObject;
import com.school_system.common.ValidationResponse;
import com.school_system.dto.request.LessonRequest;
import com.school_system.dto.request.SignatureRequest;
import com.school_system.dto.response.LessonResponse;
import com.school_system.dto.response.StudentResponse;
import com.school_system.entity.school.*;
import com.school_system.entity.school.Views.LessonVersionResponse;
import com.school_system.entity.school.Views.LessonsTable;
import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.school.FileCategory;

import com.school_system.exception.LessonValidationException;
import com.school_system.mapper.Mapper;
import com.school_system.repository.*;
import com.school_system.service.LessonService;
import com.school_system.service.StudentService;
import com.school_system.util.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.school_system.mapper.Mapper.toLessonResponse;
import static com.school_system.mapper.Mapper.toLessonVersion;

@Slf4j
@Service
@EnableCaching
@AllArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final StudentService studentService;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final SignatureRepository signatureRepository;
    private final SecurityServiceImp securityService;
    private final  EmailService emailService;
    private final LessonVersionRepository lessonVersionRepository;
    private final LessonVersionResponseRepository lessonVersionResponseRepository;
    private HttpServletResponse httpServletResponse;

    @Override
    @Transactional
    @CachePut(value = "lessons", key = "#result.data.id",  condition = "false")
    public ResponseObject<LessonResponse> saveLesson(LessonRequest lessonRequest){

        // check that at least one student is present
        if(lessonRequest.getStudents() == null || lessonRequest.getStudents().isEmpty()){
            throw new LessonValidationException("Schüler/in sind erforderlich!");
        }
        // check that there is a teacher
        if(lessonRequest.getTeacher() == null || lessonRequest.getTeacher().getId() == null){
            throw new LessonValidationException("Lehrer ist Pflichtfeld!");
        }

        if(!teacherRepository.existsById(lessonRequest.getTeacher().getId())){
            throw new EntityNotFoundException("Lehrer nicht gefunden.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        Long userId = UserUtils.getUserId(authentication);
        // check if the user is not admin then it has to be the same Teacher that gives the lesson
        if(!isAdmin && !userId.equals(lessonRequest.getTeacher().getId())){
            String errorMsg =  "Unbefugter Zugriff (Code: T-3000) erkannt. Ihr Versuch wurde an den Administrator gemeldet.";
            emailService.sendErrorNotification(errorMsg
                   , String.format("Unbefugter Teacher(%d) Zugriff",userId));
            throw new AccessDeniedException(errorMsg);
        }



        LessonValidationResponse validationResponse = checkIfLessonValid(lessonRequest);
        if (!validationResponse.isValid()){
            throw new LessonValidationException(validationResponse.getMessage());
        }
        Lesson lesson = new Lesson();
        BeanUtils.copyProperties(lessonRequest, lesson, "students");
        lessonRequest.getStudents().parallelStream().forEach(student -> {
        /*
             NOTE: this is already done by validation
            Student student1 = studentRepository.findById(student.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Schüler/in mit ID " + student.getId() + " nicht gefunden"));
         */
            Student studentEntity = new Student();
            studentEntity.setId(student.getId());
            lesson.addStudent(studentEntity,  validationResponse.getStudentModuls().get(student.getId()));
        });


        lesson.setUpdatedAt(LocalDateTime.now());
        lesson.setUpdatedBy(UserUtils.getUsername(authentication));
        return  ResponseObject.<LessonResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toLessonResponse(lessonRepository.save(lesson)))
                .build();

    }

    private LessonValidationResponse checkIfLessonValid(LessonRequest lessonRequest) {
        Map<Long, Modul> map = new HashMap<>();
        for (StudentResponse studentResponse : lessonRequest.getStudents()) {
            ValidationResponse response = studentService.checkIfStudentCanTakeLesson(studentResponse.getId(), lessonRequest );

            if (!response.isValid()) {
                return new LessonValidationResponse(false, response.getMessage(), null);
            }
            map.put(studentResponse.getId(), response.getModul());
        }
        return new LessonValidationResponse(true, "", map);
    }

    /***
     * Change the logic iof this implementation
     * adding new Students to lessons has to be done in a separate method and endpoint
     * check for lesson validity has to be done in a separate method (module, units)
     * ***/
    @Override
    @Transactional
    @CachePut(value = "lessons", key = "#result.data.id",  condition = "false")
    public ResponseObject<LessonResponse> updateLesson(LessonRequest lessonRequest) {
        Long lessonId = lessonRequest.getId();

        if(lessonId == null) {
            throw new EntityNotFoundException("Unterricht Id ist Pflichtfeld!");
        }
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Unterricht nicht gefunden!"));

        LessonVersion oldLesson = Mapper.toLessonVersion(lesson);

        // Optionally update the teacher if it has changed
        if ((lesson.getTeacher() == null && lessonRequest.getTeacher() != null) ||
                (lesson.getTeacher() != null && lessonRequest.getTeacher() != null
                        &&  !Objects.equals(lesson.getTeacher().getId(), lessonRequest.getTeacher().getId()))){
            Teacher teacher = teacherRepository.findById(lessonRequest.getTeacher().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Lehrer wurde nicht gefunden!"));
            // TODO is there need to assign the studnet to the Teacher?
            lesson.setTeacher(teacher);
        }

        // Update lesson properties
        BeanUtils.copyProperties(lessonRequest, lesson, "students");
        lesson.getSignature().clear();
        /*
        lesson.setModulType(lessonRequest.getModulType());
        lesson.setStartAt(lessonRequest.getStartAt());
        lesson.setUnits(lessonRequest.getUnits());
        lesson.setLessonType(lessonRequest.getLessonType());
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setComment(lessonRequest.getComment());*/

        // TODO optimize this checks
        if (checkIfStudentsChanged(lesson, lessonRequest)) {

            // Check if the Students can take the Lesson or not
            List<StudentLesson> studentLessonList = new ArrayList<>();
            lessonRequest.getStudents().forEach(student -> {
                ValidationResponse validationResponse = studentService.checkIfStudentCanTakeLesson(student.getId(), lessonRequest);
                Student student1 = new Student();
                student1.setId(student.getId());
                StudentLesson studentLesson = StudentLesson.builder()
                        .modul(validationResponse.getModul())
                        .student(student1)
                        .build();
                studentLessonList.add(studentLesson);
                if (!validationResponse.isValid()) {
                    throw new LessonValidationException(validationResponse.getMessage());
                }
            });
            // FIXME: try to solve this issue without clearing the students
            lesson.getStudentLessons().clear();
            lessonRepository.saveAndFlush(lesson);
            studentLessonList.forEach(st -> st.setLesson(lesson));
            lesson.setStudentLessons(studentLessonList);
        }else{
            lesson.getStudentLessons().forEach(studentLesson -> {
                ValidationResponse validationResponse = null;
                validationResponse = studentService.checkIfStudentCanTakeLesson(studentLesson.getId().getStudentId(), lessonRequest);

                if (!validationResponse.isValid()) {
                    throw new LessonValidationException(validationResponse.getMessage());
                }
            });
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        lesson.setUpdatedBy(UserUtils.getUsername(authentication));
        lesson.setUpdatedAt(LocalDateTime.now());
        lessonRepository.save(lesson);
        lessonVersionRepository.save(oldLesson);
        return ResponseObject.<LessonResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toLessonResponse(lesson))
                .build();


    }

    private boolean checkIfStudentsChanged(Lesson lesson, LessonRequest lessonRequest) {
        Set<Long> existingStudentIds = lesson.getStudentLessons().stream()
                .map(sl -> sl.getId().getStudentId())
                .collect(Collectors.toSet());

        Set<Long> updatedStudentIds = lessonRequest.getStudents().stream()
                .map(StudentResponse::getId)
                .collect(Collectors.toSet());

        return !existingStudentIds.equals(updatedStudentIds);
    }




    @Override
    public ResponseObject<Page<LessonResponse>> getStudentLessons(Long studentId, Pageable pageable) {
        Page<LessonResponse> lessonsPage = lessonRepository.findByStudentId(studentId , pageable).map(Mapper::toLessonResponse);
        if (lessonsPage.isEmpty()) {
            return ResponseObject.<Page<LessonResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Der Schüler hat keine Lektionen!")
                    .build();
        }
        return ResponseObject.<Page<LessonResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(lessonsPage)
                .build();
    }


    @Override
    @Cacheable(value = "lessons", key = "'lessons.'+#id",  condition = "false")
    public ResponseObject<LessonResponse> getLesson(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();
        boolean isStudent = securityService.isStudent();

        LessonResponse lesson = null;
        if (isAdmin){
            lesson = lessonRepository.findById(id).map(Mapper::toLessonResponse)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Lesson not found!")
                    );
        } else if (isTeacher) {
            Long teacherId = UserUtils.getUserId(authentication);
             lesson = lessonRepository.findLessonByTeacherId(id, teacherId).map(Mapper::toLessonResponse).orElseThrow(
                    () -> new EntityNotFoundException("Lesson not found!")
            );
        }else if (isStudent) {
            Long studentId = UserUtils.getUserId(authentication);

            lesson = lessonRepository.findLessonByStudentId(id, studentId).map(Mapper::toLessonResponse).orElseThrow(
                    () -> new EntityNotFoundException("Lesson not found!")
            );
        }else{
            throw new AccessDeniedException("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!");

        }
        List<FileMetadata> files = fileMetaDataRepository.findByEntityIdAndFileCategory(id, FileCategory.HOMEWORK);
        lesson.setFiles(files);
        return ResponseObject.<LessonResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(lesson)
                .build();

    }

    @Override
    public ResponseObject<Page<LessonsTable>> getLessonsWithQuery(Long userId, String query, String filter, Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        boolean isTeacher = securityService.isTeacher();
        boolean isStudent = securityService.isStudent();
        long id = UserUtils.getUserId(authentication);
        if(!isAdmin && !isTeacher && !isStudent){
            throw new AccessDeniedException("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!");
        }
        Page<LessonsTable> lessons;
        if (isAdmin){

            lessons = userId != null ?
                    lessonRepository.searchLessonsByStudentId(query,userId, pageable).map(Mapper::toLessonsTable) :
                    lessonRepository.searchLessons(query, pageable).map(Mapper::toLessonsTable);
        } else if (isTeacher) {
            lessons =
                    lessonRepository.searchLessonsByTeacherId(query,id,userId, pageable).map(Mapper::toLessonsTable);
        }else {
             lessons =
                    lessonRepository.searchLessonsByStudentId(query,id, pageable).map(Mapper::toLessonsTable);
        }

        return ResponseObject.<Page<LessonsTable>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(lessons)
                .build();
    }

    @Override
    public ResponseObject<List<LessonVersionResponse>> getLessonVersions(long lessonId) {
        List<LessonVersionResponse> lessonVersions = lessonVersionResponseRepository.findByLessonIdOrderByCreatedAtDesc(lessonId);


        return ResponseObject.<List<LessonVersionResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(lessonVersions)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "lessons", key = "#id",  condition = "false")
    public ResponseObject<String> deleteLesson(Long id) {

        try {
            Lesson lesson = lessonRepository.findById(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Lesson with id = %d not found!", id)));

            lessonRepository.delete(lesson);

            return ResponseObject.<String>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Lektion erfolgreich gelöscht!")
                    .build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    @CachePut(value = "lessons", key = "#signautre.getLessonId()",  condition = "false")
    public ResponseObject<LessonResponse> signLesson( SignatureRequest signautre) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = UserUtils.getUserId(authentication);

        String base64Data = signautre.getSignature().split(",")[1];
        // Decode base64 data to get byte array
        byte[] signatureData = Base64.getDecoder().decode(base64Data);
        Lesson lesson = lessonRepository.findById(signautre.getLessonId()).orElseThrow(
                () -> new LessonValidationException("Unterrricht nicht gefunden!")
        );
        if(!userId.equals(lesson.getTeacher().getId())){
            // TODO throw an Error and centrelaize the message
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<LessonResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }
        Signature signature = Signature.builder()
                .signature(signatureData)
                .signedAt(java.time.LocalDateTime.now())
                .ipAddress(UserUtils.getClientIP())
                .signedBy(UserUtils.getUsername(SecurityContextHolder.getContext().getAuthentication()))
                .build();

        lesson.addSignautre(signature);
        signatureRepository.save(signature);
        lessonRepository.save(lesson);
        return ResponseObject.<LessonResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toLessonResponse(lesson))
                .message("Unterricht erfolgreich unterschrieben!")
                .build();
    }

    @Override
    public ResponseObject<LessonResponse> signLessonByStudent(SignatureRequest signautre) {
        return null;
    }


}
