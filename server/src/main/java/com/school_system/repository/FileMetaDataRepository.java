package com.school_system.repository;

import com.school_system.entity.school.FileMetadata;
import com.school_system.enums.school.FileCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetaDataRepository extends JpaRepository<FileMetadata, Long>{

    List<FileMetadata> findByEntityIdAndFileCategory(Long entityId, FileCategory fileCategory);

    Optional<FileMetadata> findByFileName(String originalFilename);

}
