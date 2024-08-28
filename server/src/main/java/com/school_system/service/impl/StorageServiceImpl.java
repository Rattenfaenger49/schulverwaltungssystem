package com.school_system.service.impl;


import com.school_system.config.properties.StorageProperties;
import com.school_system.entity.school.FileMetadata;
import com.school_system.exception.StorageException;
import com.school_system.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {

        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("Der Speicherort für den Datei-Upload existiert nicht.");
        }

        this.rootLocation = Paths.get(properties.getLocation());

    }

    @Override
    public void store(MultipartFile file, String path) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Datei ist leer.");
            }

            Path destinationFile = resolveDestinationFile(file.getOriginalFilename(), path);
            ensureDirectoryExists(destinationFile.getParent());

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Fehler beim Speichern der leeren Datei.", e);
        }
    }

    @Override
    public void store(byte[] file, FileMetadata fileMetaData) {
        try {
            if (file == null || file.length == 0) {
                throw new StorageException("Dateiinhalt ist leer.");
            }

            int lastIndexOfSeparator = fileMetaData.getFullPath().lastIndexOf("/");
            String directoryPath = fileMetaData.getFullPath().substring(0, lastIndexOfSeparator);

            Path destinationFile = resolveDestinationFile(fileMetaData.getFileName(), directoryPath);
            ensureDirectoryExists(destinationFile.getParent());

            try (OutputStream outputStream = Files.newOutputStream(destinationFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                outputStream.write(file);
            }
        } catch (IOException e) {
            throw new StorageException("Fehler beim Speichern der Datei.", e);
        }
    }

    private Path resolveDestinationFile(String originalFilename, String path) {
        Path destinationFolder = this.rootLocation.resolve(path).normalize().toAbsolutePath();

        if (!destinationFolder.startsWith(this.rootLocation.toAbsolutePath())) {
            throw new StorageException("Die Datei kann nicht außerhalb des aktuellen Verzeichnisses gespeichert werden.");
        }


        return destinationFolder.resolve(originalFilename).normalize().toAbsolutePath();

    }
    private Path resolveDestinationFile(String fullPath) {
        Path destinationFolder = this.rootLocation.resolve(fullPath).normalize().toAbsolutePath();

        if (!destinationFolder.startsWith(this.rootLocation.toAbsolutePath())) {
            throw new StorageException("Die Datei kann nicht außerhalb des aktuellen Verzeichnisses gespeichert werden.");
        }


        return destinationFolder.normalize().toAbsolutePath();

    }

    private void ensureDirectoryExists(Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new StorageException("Fehler beim Erstellen des Verzeichnisses: " + directory, e);
        }
    }


    @Override
    public Stream<Path> loadAll() throws IOException {

        return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);


    }

    @Override
    @Cacheable(value = "files", key = "#fullPath",  condition = "false")
    public byte[] load(String fullPath) {
        try {
            // Resolve the file path within the root location
            Path path = resolveDestinationFile(fullPath);


            // Load the file as a Resource
            Resource resource = new UrlResource(path.toUri());
            log.debug("resource: {}", resource);

            // Check if the file exists and is readable
            if (resource.exists() && resource.isReadable()) {
                log.debug("resource.exists() && resource.isReadable() : true");
                // Read the file content into a byte array
                return FileCopyUtils.copyToByteArray(resource.getInputStream());

            } else {
                log.debug("File not found: {} ", fullPath);
                // CHECK_IF_NEEDED fileMetaDataRepository.delete(file);
                log.debug("FileMetaData deleted from DB!");
                // File not found or not readable, throw a 404 Not Found exception
                throw new EntityNotFoundException("Datei wurde nicht gefunden");
            }
        } catch (IOException e) {
            // Error occurred while loading the file, throw a 500 Internal Server Error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ein Fehler ist aufgetreten", e);
        }
    }

    @Override
    @Cacheable(value = "files", key = "#filename",  condition = "false")
    public Resource loadAsResource(String filename) {
        // TODO check compatibilty with frontend implementation!
        try {
            // Resolve the file path
            Path filePath = rootLocation.resolve(filename).normalize();
            // Create a UrlResource from the file path
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the resource exists and is readable
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read file: " + filename, ex);
        }
    }


    @Override
    @CacheEvict(value = "files", key = "#fullPath",  condition = "false")
    public boolean deleteFile(String fullPath) {
        Path fileToDelete = resolveDestinationFile(fullPath);

        if (!Files.exists(fileToDelete)) {
            return false;
        }

        try {
            Files.delete(fileToDelete);
            return true;
        } catch (IOException e) {
            // Fehlerbehandlung
            throw new StorageException("Fehler beim Löschen der Datei: " + fullPath, e);
        }
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Speicher konnte nicht initialisiert werden.", e);
        }
    }
}
