package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.InstitutionRequest;
import com.school_system.dto.response.InstitutionResponse;
import com.school_system.service.InstitutionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@Slf4j
@AllArgsConstructor
@RestController
@PreAuthorize("@securityService.isAdmin()")
@RequestMapping("/api/v1/institutions")

public class InstitutionController {


    private final InstitutionService institutionService;


    @PostMapping
    public ResponseObject<InstitutionResponse> createInstitution(@RequestBody InstitutionRequest institutionRequest){

        return institutionService.createInstitution(institutionRequest);

    }
    @PutMapping
    public ResponseObject<InstitutionResponse> updateInstitution(@RequestBody InstitutionRequest institutionRequest){

        return institutionService.updateInstitution(institutionRequest);

    }
    @GetMapping("/{id}")
    public ResponseObject<InstitutionResponse> getInstitution(@PathVariable Long id){

        return institutionService.getInstitution(id);

    }
    @GetMapping
    public ResponseObject<Page<InstitutionResponse>> getInstitutions(@RequestParam String query, @PageableDefault(
            sort = "name", direction = Sort.Direction.ASC
    ) Pageable pageable) {
        if (query != null && !query.isEmpty()) {
            return institutionService.getInstitutionsWithQuery(query, pageable);
        }
        return institutionService.getInstitutions(pageable);

    }
    @DeleteMapping("/{id}")
    public ResponseObject<String> deleteInstitution(@PathVariable Long id){

        return institutionService.deleteInstitution(id);

    }
}
