package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.LessonRequest;
import com.school_system.dto.request.SignatureRequest;
import com.school_system.dto.response.LessonResponse;
import com.school_system.entity.school.Views.LessonVersionResponse;
import com.school_system.entity.school.Views.LessonsTable;
import com.school_system.service.LessonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
@RequestMapping("/api/v1/lessons")
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseObject<LessonResponse> getLesson(@PathVariable Long id){
        return lessonService.getLesson(id);

    }
    @GetMapping("/{id}/archive")
    public ResponseObject<List<LessonVersionResponse>> getLessonVersions(@PathVariable long id){
        return lessonService.getLessonVersions(id);

    }

    @GetMapping
    public ResponseObject<Page<LessonsTable>> getLessons(@RequestParam(defaultValue = "") String query,
                                                         @RequestParam(defaultValue = "") String filter,
                                                         @RequestParam(defaultValue = "") String id,

                                                         @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        // if set the
        Long userId;
        try{
            userId = Long.parseLong(id);
        }catch (Exception e){
            // Do nothing while studentId is already null
            userId = null;
        }
        return lessonService.getLessonsWithQuery(userId, query.trim(),filter.trim(), pageable);

    }
// TODO merge with get Lessons
    @GetMapping("/student/{studentId}")
    public ResponseObject<Page<LessonResponse>> getStudentLessons(@PageableDefault(
            sort = "id", direction = Sort.Direction.DESC
    ) Pageable pageable, @PathVariable Long studentId){
        return lessonService.getStudentLessons(studentId, pageable);

    }

    @PostMapping
    @PreAuthorize("@securityService.isAdmin() || @securityService.isTeacher() ")
    public ResponseObject<LessonResponse> addLesson(@RequestBody LessonRequest lessonRequest) throws BadRequestException {
        return lessonService.saveLesson(lessonRequest);

    }

    @PutMapping
    @PreAuthorize("@securityService.isSameUserOrAdmin(#lessonRequest.teacher.id)")
    public ResponseObject<LessonResponse> updateLesson(@RequestBody LessonRequest lessonRequest){
        return lessonService.updateLesson(lessonRequest);

    }
    @PostMapping("/{id}/signature")
    public ResponseObject<LessonResponse> signLesson(@RequestBody @Valid SignatureRequest signautre){

        return lessonService.signLesson(signautre);
    }
    @PreAuthorize("@securityService.isAdmin()")
    @DeleteMapping("/{id}")
    public ResponseObject<String> deleteLesson(@PathVariable Long id){
        return lessonService.deleteLesson(id);

    }

}
