/*

package com.school_system.util;
*/
/***
 * optimize th e configuration of the GCP bucket
 * implement the logic of getting all files in a specific folder
 *  Iterable<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPrefix)).iterateAll();
 *    // Iterate over the objects and print their names
 *    for (Blob blob : blobs) {
 *         System.out.println(blob.getName());
 *    }
 *
 *
 * ****//*



import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.school_system.entity.school.FileMetadata;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@Component
public class DataBucketUtil {


    @Value("${gcp.config.file}")
    private  String gcpConfigFile;

    @Value("${gcp.project.id}")
    private   String gcpProjectId;

    @Value("${gcp.bucket.id}")
    private  String gcpBucketId;

    @Value("${gcp.dir.name}")
    private  String gcpDirectoryName;


    public  Blob uploadFile(MultipartFile file, String contentType, String path, String tenantId) {

        try{
            byte[] fileData = file.getBytes();
            InputStream inputStream = new ByteArrayInputStream(gcpConfigFile.getBytes());
            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();

            Storage storage = options.getService();
            Bucket bucket = storage.get(gcpBucketId, Storage.BucketGetOption.fields());
            Blob blob = bucket.create( tenantId + "/" + path + "/"+ file.getOriginalFilename() , fileData, contentType);
            if(blob != null){
                return blob;
            }

        }catch (Exception e){
            log.error("An error occurred while storing data to GCS", e);
            throw new RuntimeException("An error occurred while storing data to GCS");
        }
        throw new RuntimeException("Datei konnte nicht gespeichert werden");
    }
    public String retrieveFile(FileMetadata file) throws IOException {
        try {


        String objectName = file.getFullPath();
        InputStream inputStream = new ByteArrayInputStream(gcpConfigFile.getBytes());

        StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
        Storage storage = options.getService();
        // Retrieve the file from Google Cloud Storage
        BlobInfo blobInfo = BlobInfo.newBuilder(gcpBucketId, objectName).build();
        String fileUrl = storage.signUrl(blobInfo, 5, java.util.concurrent.TimeUnit.MINUTES).toString();

        // Read the file content as bytes
        if (fileUrl != null) {
            return fileUrl;
        } else {
            // Handle case when blob is not found
            log.error("File wurde nicht gefunden");
           throw new EntityNotFoundException("File not found");
        }
        }catch ( Exception e){
            throw new RuntimeException("Etwas ist schief gelaufen");

        }
    }

    public byte[] retrieveFileAsBlob(FileMetadata file) {
        try {

            String objectName = file.getFullPath();
            InputStream inputStream = new ByteArrayInputStream(gcpConfigFile.getBytes());

            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
            Storage storage = options.getService();
            BlobId blobId = BlobId.of(gcpBucketId, objectName);
            Blob blodFile = storage.get(blobId);


            // Read the file content as bytes
            if (blodFile != null) {
                return blodFile.getContent();
            } else {
                // Handle case when blob is not found
                log.error("File wurde nicht gefunden");
                throw new EntityNotFoundException("File not found");
            }
        }catch ( Exception e){
            throw new RuntimeException("Etwas ist schief gelaufen");

        }

    }
    // TODO test the delete file method
    public boolean deleteFile(FileMetadata file) throws IOException {
        try {

            String objectName = file.getFullPath();
            InputStream inputStream = new ByteArrayInputStream(gcpConfigFile.getBytes());
            StorageOptions options = StorageOptions.newBuilder().setProjectId(gcpProjectId)
                    .setCredentials(GoogleCredentials.fromStream(inputStream)).build();
            Storage storage = options.getService();
            // Retrieve the file from Google Cloud Storage
            BlobId blobId = BlobId.of(gcpBucketId, objectName);
            if(storage.get(blobId) == null)
                throw new EntityNotFoundException("File not found");

            return storage.delete(blobId);
        }catch ( Exception e){
            throw new RuntimeException("Etwas ist schief gelaufen");

        }
    }



    private  String checkFileExtension(String fileName) {
        if(fileName != null && fileName.contains(".")){
            String[] extensionList = {".png", ".jpeg", ".pdf", ".doc", ".mp3", "txt",
            ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".mp4"};

            for(String extension: extensionList) {
                if (fileName.endsWith(extension)) {
                    return extension;
                }
            }
        }
        log.error("Not a permitted file type");
        throw new RuntimeException("Not a permitted file type");
    }


}
*/
