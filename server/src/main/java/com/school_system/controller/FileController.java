package com.school_system.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.school_system.common.ResponseObject;
import com.school_system.dto.request.FileMetaDataRequest;
import com.school_system.entity.school.FileMetadata;

import com.school_system.service.FileService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/files")

public class  FileController {


    private final FileService fileService;

    @GetMapping("/{fileId}")
    public ResponseObject<byte[]> getFile(@PathVariable Long fileId){
        return fileService.getFile(fileId);

    }

    @PostMapping
    public ResponseObject<?> uplaodFiles(@RequestParam("file") @NotNull MultipartFile file,
                                                    @RequestParam("metadata") @NotNull String metadata) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        FileMetaDataRequest fileMetaDataRequest = objectMapper.readValue(metadata, FileMetaDataRequest.class);
        return fileService.uploadFile(file,
                fileMetaDataRequest);
    }
    @DeleteMapping("/{fileId}")
    public ResponseObject<String> deleteFile(@PathVariable Long fileId) throws IOException {
        return fileService.deleteFile(fileId);
    }

}
