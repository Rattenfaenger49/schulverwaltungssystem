package com.school_system.service;


import com.school_system.common.ResponseObject;
import com.school_system.dto.request.LessonRequest;
import com.school_system.dto.request.SignatureRequest;
import com.school_system.dto.response.LessonResponse;

import com.school_system.entity.school.Views.LessonVersionResponse;
import com.school_system.entity.school.Views.LessonsTable;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface LessonService {
    ResponseObject<LessonResponse> saveLesson(LessonRequest lessonRequest) throws BadRequestException;
    ResponseObject<LessonResponse> updateLesson(LessonRequest lessonRequest);
    ResponseObject<Page<LessonResponse>> getStudentLessons(Long studentId, Pageable pageable);


    ResponseObject<LessonResponse> getLesson(Long id);

    ResponseObject<Page<LessonsTable>> getLessonsWithQuery(Long userId, String query, String filter, Pageable pageable);
    ResponseObject<List<LessonVersionResponse>> getLessonVersions(long lessonId);

    ResponseObject<String> deleteLesson(Long id);

    ResponseObject<LessonResponse> signLesson(SignatureRequest signautre);

    ResponseObject<LessonResponse> signLessonByStudent(SignatureRequest signautre);





}
