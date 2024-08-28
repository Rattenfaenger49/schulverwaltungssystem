package com.school_system.controller;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.ContractRequest;
import com.school_system.dto.response.ContractResponse;
import com.school_system.entity.school.Contract;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import com.school_system.service.ContractService;
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
@PreAuthorize("@securityService.isAdmin()")
@RequestMapping("/api/v1/contracts")

public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("@securityService.isAdmin() || @securityService.isStudent()")
    public ResponseObject<Page<ContractResponse>> getContracts(@RequestParam(defaultValue = "") String query,
                                                               @Valid @RequestParam(required = false) ContractType contractType,
                                                               @Valid @RequestParam(required = false) ContractStatus contractStatus,
                                                               @PageableDefault(
            sort = "id", direction = Sort.Direction.ASC
    ) Pageable pageable){
        log.info("getContracts: {}", contractType);
        return  contractService.getContractsWithQuery(query, contractType,contractStatus, pageable);
    }
    @GetMapping("/{id}")
    public ResponseObject<ContractResponse> getContractById(@PathVariable Long id){

        return contractService.getContract(id);

    }
    @GetMapping("/student/{studentId}")
    public ResponseObject<List<ContractResponse>> getContractByStudentId(@PathVariable Long studentId){
        return  contractService.getContractByStudentId(studentId);

    }
    @PostMapping
    public ResponseObject<ContractResponse> createContract(@RequestBody ContractRequest contract) throws BadRequestException {

        return contractService.createContract(contract);

    }
    @PutMapping("/{id}")
    public ResponseObject<ContractResponse> updateContract(@PathVariable Long id, @RequestBody ContractRequest contract) throws BadRequestException {

        return contractService.updateContract(id, contract);

    }
    @PreAuthorize("@securityService.isAdmin()")
    @DeleteMapping("/{id}")
    public ResponseObject<String> deleteContract(@PathVariable Long id){
        return contractService.deleteContract(id);

    }
}
