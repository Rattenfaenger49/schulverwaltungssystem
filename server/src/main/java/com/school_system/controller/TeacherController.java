package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.TeacherUpdateRequest;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserFullNameResponse;
import com.school_system.dto.response.UserTable;
import com.school_system.service.TeacherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<Page<UserTable>> getTeachersWithQuery(@RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "") String filter,@PageableDefault(
            sort = "firstName", direction = Sort.Direction.ASC
    ) Pageable pageable){
        if (query.isEmpty()  && filter.isEmpty()){
            return teacherService.getTeachers(pageable);
        }
        return teacherService.getTeachersWithQuery(query.trim(), filter.trim(), pageable);

    }
    @GetMapping("/{id}")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<TeacherResponse> getTeacherById(@PathVariable Long id){

        return teacherService.getTeacherById(id);

    }
    @PutMapping
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<TeacherResponse> updateTeacher(@RequestBody TeacherUpdateRequest teacher){
        return teacherService.updateTeacher(teacher);

    }
    @PutMapping("/{id}/undo-delete")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<TeacherResponse> undoDeleteTeacher(@PathVariable Long id){
        return teacherService.undoDeleteTeacher(id);

    }


    @GetMapping("/fullnames")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<List<UserFullNameResponse>> getTeachersFullname(){

        return teacherService.getTeachersFullname();

    }

    @PostMapping("/{teacherId}/{studentId}")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<TeacherResponse> assignStudentToTeacher(@PathVariable Long teacherId, @PathVariable Long studentId){

        return teacherService.assignStudentToTeacher(teacherId,studentId);
    }
    @DeleteMapping("/{id}")
    public ResponseObject<TeacherResponse> deleteTeacher(@PathVariable Long id){

        return teacherService.deleteTeacher(id);

    }
    @DeleteMapping("/{teacherId}/{studentId}")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<TeacherResponse> removeStudentToTeacherAssignment(@PathVariable Long teacherId, @PathVariable Long studentId){

        return teacherService.removeStudentToTeacherAssignment(teacherId,studentId);
    }
}
