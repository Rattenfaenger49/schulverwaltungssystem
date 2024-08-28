package com.school_system.service;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.ContractRequest;
import com.school_system.dto.response.ContractResponse;
import com.school_system.entity.school.Contract;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContractService {
    ResponseObject<ContractResponse> createContract(ContractRequest contract) throws BadRequestException;

    ResponseObject<ContractResponse> getContract(Long id);

    ResponseObject<Page<ContractResponse>> getContracts(Pageable pageable);

    ResponseObject<List<ContractResponse>> getContractByStudentId(Long studentId);

    ResponseObject<ContractResponse> updateContract(Long id, ContractRequest contract) throws BadRequestException;

    ResponseObject<String> deleteContract(Long id);

    ResponseObject<Page<ContractResponse>> getContractsWithQuery(String query, ContractType contractType, ContractStatus contractStatus, Pageable pageable);
}
