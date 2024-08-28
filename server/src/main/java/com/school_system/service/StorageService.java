package com.school_system.service;


import com.school_system.entity.school.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();

    void store(MultipartFile file, String path);
    void store(byte[] file, FileMetadata fileMetaData);

    Stream<Path> loadAll() throws IOException;

    byte[] load(String fullPath);

    Resource loadAsResource(String filename);

    boolean deleteFile(String filePath) throws IOException;
}
