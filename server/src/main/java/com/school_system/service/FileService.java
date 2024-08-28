package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.FileMetaDataRequest;
import com.school_system.entity.school.FileMetadata;
import com.school_system.entity.school.Invoice;
import com.school_system.entity.school.Signature;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public interface FileService {

    ResponseObject<byte[]> getFile(Long fileId);
    ResponseObject<?> uploadFile(MultipartFile file, FileMetaDataRequest fileMetadata);
    ResponseObject<String> deleteFile(Long fileId) throws IOException;

    byte[] addSignatureToInvoice(Invoice invoice, Signature signature);
}
