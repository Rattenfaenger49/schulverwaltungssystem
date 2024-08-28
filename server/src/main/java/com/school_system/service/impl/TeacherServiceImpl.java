package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.TeacherUpdateRequest;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.dto.response.UserTable;

import com.school_system.entity.security.Student;
import com.school_system.entity.security.Teacher;
import com.school_system.enums.authentication.RoleName;
import com.school_system.mapper.Mapper;
import com.school_system.repository.StudentRepository;
import com.school_system.repository.TeacherRepository;
import com.school_system.service.AddressService;
import com.school_system.service.TeacherService;
import com.school_system.util.UserUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AddressService addressService;
    private final HttpServletResponse httpServletResponse;
    private final RedisTemplate redisTemplate;
    private final CacheManager cacheManager;
    private final SecurityServiceImp securityService;

    @Override
    @Cacheable(value = "teachers", key = "'names'",  condition = "false")
    public ResponseObject<List<UserFullNameResponse>> getTeachersFullname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = securityService.isAdmin();

        if (!isAdmin) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                    .build();
        }
        List<UserFullNameResponse> userListResponses = teacherRepository.findAllWithFullname();
        if(userListResponses.isEmpty()){
            return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Keine Lehrer gefunden!!")
                    .build();
        }
        return ResponseObject.<List<UserFullNameResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(userListResponses)
                    .build();


    }

    @Override
    public ResponseObject<Page<UserTable>> getTeachersWithQuery(String query, String filter, @PageableDefault(
            sort = "firstName", direction = Sort.Direction.ASC
    )Pageable pageable) {
        Page<Teacher> teachers = teacherRepository.searchTeachers(filter, query, pageable);

        return ResponseObject.<Page<UserTable>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(teachers.map(Mapper::toUserTable))
                .build();
    }


    @Override
    @Transactional
    @Cacheable(value = "teachers", key = "#id",  condition = "false")
    public ResponseObject<TeacherResponse> getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Teacher with id = %d not found!", id))
                );
        // Check if nötig
        return ResponseObject.<TeacherResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toTeacherResponse(teacher))
                .build();

    }

    @Override
    public ResponseObject<Page<UserTable>> getTeachers(Pageable pageable) {
        Page<Teacher> teachersPage = teacherRepository.findAll(pageable);
        if (teachersPage.isEmpty()) {
            return ResponseObject.<Page<UserTable>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Keine Lehrer gefunden!")
                    .build();
        }

        return ResponseObject.<Page<UserTable>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(teachersPage.map(Mapper::toUserTable))
                .build();

    }

    @Transactional
    @Override
    @CachePut(value = "teachers", key = "#result.data.id",  condition = "false")
    public ResponseObject<TeacherResponse> updateTeacher(TeacherUpdateRequest teacherReuest) {
        Teacher teacher = teacherRepository.findById(teacherReuest.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Lehrer mit der ID = %d wurde nicht gefunden!", teacherReuest.getId())));

        if(teacher.isMarkedForDeletion()){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Vertrag kann nicht erstellt werden, weil der Lehrer zur Löschung markiert ist!")
                    .build();
        }
        BeanUtils.copyProperties(teacherReuest, teacher, "address");
        if (!teacher.getAddress().equals(teacherReuest.getAddress())) {
            teacher.setAddress(addressService.getExistingOrSaveNewAddress(teacherReuest.getAddress()));
        }
        teacherRepository.save(teacher);

        return ResponseObject.<TeacherResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toTeacherResponse(teacher))
                .build();

    }
    @Transactional
    @Override
    @CachePut(value = "teachers", key = "#result.data.id",  condition = "false")
    public ResponseObject<TeacherResponse> assignStudentToTeacher(Long teacherId, Long studentId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Teacher with id = %d not found!", teacherId))
        );

        if(teacher.isMarkedForDeletion()){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Lehrer wurde zum Löschen markiert und kann nicht aktualisiert werden!")
                    .build();
        }

        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Student with id = %d not found!", studentId))
        );

        if(teacher.getStudents().contains(student)){
            httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Schüler ist bereits einem Lehrer zugeordnet!")
                    .data(Mapper.toTeacherResponse(teacher))
                    .build();
        }
        teacher.addStudent(student);
        teacherRepository.save(teacher);
        // condition = "false"
       // cacheManager.getCache("students").evict(studentId.toString());

        studentRepository.save(student);
        return ResponseObject.<TeacherResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Schüler erfolgreich einem Lehrer zugewiesen!")
                .data(Mapper.toTeacherResponse(teacher))
                .build();
    }

    @Override
    @CachePut(value = "teachers", key = "#teacherId",  condition = "false")
    public ResponseObject<TeacherResponse> removeStudentToTeacherAssignment(Long teacherId, Long studentId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                () ->
                        new EntityNotFoundException("Lehrer nicht gefunden!")
        );

        Student student = studentRepository.findById(studentId).orElseThrow(
                () ->
                        new EntityNotFoundException("Schüler nicht gefunden!")
        );

        if(teacher.isMarkedForDeletion()){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Lehrer wurde zum Löschen markiert und kann nicht aktualisiert werden!")
                    .build();
        }

        if(!teacher.getStudents().contains(student)){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Der Schüler ist dem Lehrer nicht zugewiesen!")
                    .data(Mapper.toTeacherResponse(teacher))
                    .build();
        }
        teacher.removeStudent(student);
        teacherRepository.save(teacher);
        Set<String> keys = new HashSet<>();
        keys.add(studentId.toString());
        // condition = "false"
        // cacheManager.getCache("students").evict(studentId.toString());
        studentRepository.save(student);
        return ResponseObject.<TeacherResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Die Zuweisung des Schülers wurde erfolgreich entfernt!")
                .build();
    }
    public StringBuffer getAllKeys() {


        StringBuffer sb = new StringBuffer();
        Set<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().keys("*".getBytes());

        Iterator<byte[]> it = keys.iterator();

        while(it.hasNext()){

            byte[] data = (byte[])it.next();
            sb.append(new String(data, 0, data.length));
        }
        return sb;
    }
    @Override
    @CachePut(value = "teachers", key = "#id",  condition = "false")
    public ResponseObject<TeacherResponse> deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Lehrer mit der ID = %d wurde nicht gefunden!", id)));
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = UserUtils.getUsername(authentication);
            teacher.deleteProfile();
            teacher.setUpdatedAt(LocalDateTime.now());
            teacher.setUpdatedBy(username);
            teacherRepository.save(teacher);

            return ResponseObject.<TeacherResponse>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(Mapper.toTeacherResponse(teacher))
                    .message("Lehrer erfolgreich gelöscht!")
                    .build();
        }catch (Exception e){
            throw new RuntimeException("Lehrer konnte nicht gelöscht werden!");
        }
    }


    @Override
    @CachePut(value = "teachers", key = "#id",  condition = "false")
    public ResponseObject<TeacherResponse> undoDeleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Lehrer mit der ID = %d wurde nicht gefunden!", id)));
        teacher.undoDeleteProfile();
        teacher.setUpdatedAt(LocalDateTime.now());
        teacher.setUpdatedBy(UserUtils.getUsername(SecurityContextHolder.getContext().getAuthentication()));
        teacherRepository.save(teacher);
        return ResponseObject.<TeacherResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Lehrer erfolgreich wiederhergestellt!")
                .data(Mapper.toTeacherResponse(teacher))
                .build();
    }
}
