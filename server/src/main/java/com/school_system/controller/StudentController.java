package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.StudentUpdateRequest;
import com.school_system.dto.response.StudentResponse;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.dto.response.UserFullNameResponse;

import com.school_system.service.SecurityService;
import com.school_system.service.StudentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;
    public static Pageable addSecondarySort(Pageable pageable) {
        Sort sort = pageable.getSortOr(Sort.by("id")).and(Sort.by("id"));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
    @GetMapping
    public ResponseObject<Page<StudentResponse>> getStudents(@RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "") String filter, @PageableDefault(
            sort = "id", direction = Sort.Direction.DESC
    ) Pageable pageable){
        pageable = addSecondarySort(pageable); // Ensure secondary sort by id
        return studentService.getStudentWithQuery(pageable, query.trim(), filter.trim());
    }

    @GetMapping("/fullnames")
    public ResponseObject<List<UserFullNameResponse>> getStudentsFullname(){
        return studentService.getStudentsFullname();
    }
    @GetMapping("/{id}")
    public ResponseObject<StudentResponse> getStudentById(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @PutMapping
    @PreAuthorize("@securityService.isSameUserOrAdmin(#studentUpdateRequest.id)")
    public ResponseObject<StudentResponse> updateStudent(@RequestBody @Valid StudentUpdateRequest studentUpdateRequest){

        return studentService.updateStudent(studentUpdateRequest);

    }

    @PreAuthorize("@securityService.isAdmin()")
    @DeleteMapping("/{id}")
    public ResponseObject<StudentResponse> deleteStudent(@PathVariable Long id){
        return studentService.deleteStudent(id);
    }

    @PutMapping("/{id}/undo-delete")
    @PreAuthorize("@securityService.isAdmin()")
    public ResponseObject<StudentResponse> undoDeleteStudent(@PathVariable Long id){
        return studentService.undoDeleteStudent(id);

    }
    /***
     *     @GetMapping("/student-profile")
     *     @PreAuthorize("@securityService.isAdmin()")
     *     public ResponseObject<Student> getStudentProfileById(@RequestParam Long id){
     *
     *         return studentService.getStudentProfileById(id);
     *     }
     * **/
}
