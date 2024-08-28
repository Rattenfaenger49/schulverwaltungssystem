package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.InstitutionRequest;
import com.school_system.dto.response.InstitutionResponse;
import com.school_system.entity.school.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface InstitutionService {
    ResponseObject<InstitutionResponse> createInstitution(InstitutionRequest institutionRequest);

    ResponseObject<InstitutionResponse> getInstitution(Long id);

    ResponseObject<Page<InstitutionResponse>> getInstitutions(Pageable pageable);

    ResponseObject<InstitutionResponse> updateInstitution(InstitutionRequest institutionRequest);

    ResponseObject<Page<InstitutionResponse>> getInstitutionsWithQuery(String query, Pageable pageable);

    ResponseObject<String> deleteInstitution(Long id);
}
