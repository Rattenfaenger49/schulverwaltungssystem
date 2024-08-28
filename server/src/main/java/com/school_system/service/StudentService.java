package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.common.ValidationResponse;
import com.school_system.dto.request.LessonRequest;
import com.school_system.dto.request.StudentUpdateRequest;
import com.school_system.dto.response.StudentResponse;
import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.entity.security.Student;
import com.school_system.enums.school.ContractType;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentService {

    ResponseObject<Page<StudentResponse>> getAllStudents(Pageable pageable);
    ResponseObject<StudentResponse> getStudentById(Long id);
    ResponseObject<StudentResponse> updateStudent(StudentUpdateRequest studentUpdateRequest);
    ResponseObject<Page<StudentResponse>> getStudentWithQuery(Pageable pageable, String query, String filter);
    ResponseObject<List<UserFullNameResponse>> getStudentsFullname();
    ResponseObject<Page<StudentResponse>> getAssociatedStudentsByTeacherId(Pageable pageable);
    ValidationResponse checkIfStudentCanTakeLesson(Long studentId, LessonRequest lessonRequest);
    ResponseObject<StudentResponse> deleteStudent(Long id);

    ResponseObject<StudentResponse> undoDeleteStudent(Long id);
}

