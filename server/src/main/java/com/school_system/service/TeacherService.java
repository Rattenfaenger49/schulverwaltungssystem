package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.TeacherUpdateRequest;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.dto.response.UserTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {
    ResponseObject<TeacherResponse> getTeacherById(Long id);
    ResponseObject<Page<UserTable>> getTeachers(Pageable pageable);


    ResponseObject<TeacherResponse> updateTeacher(TeacherUpdateRequest teacherUpdateRequest);
    ResponseObject<List<UserFullNameResponse>> getTeachersFullname();


    ResponseObject<Page<UserTable>> getTeachersWithQuery(String query,String filter, Pageable pageable);
    ResponseObject<TeacherResponse> assignStudentToTeacher(Long teacherId, Long studentId);

    ResponseObject<TeacherResponse> removeStudentToTeacherAssignment(Long teacherId, Long studentId);

    ResponseObject<TeacherResponse> deleteTeacher(Long id);


    ResponseObject<TeacherResponse> undoDeleteTeacher(Long id);
}
