package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.ContractRequest;
import com.school_system.dto.response.ContractResponse;
import com.school_system.entity.school.Contact;
import com.school_system.entity.school.Contract;
import com.school_system.entity.school.Modul;
import com.school_system.entity.security.Student;
import com.school_system.enums.school.ContractStatus;
import com.school_system.enums.school.ContractType;
import com.school_system.mapper.Mapper;
import com.school_system.repository.ContactRepository;
import com.school_system.repository.ContractRepository;
import com.school_system.repository.StudentRepository;
import com.school_system.service.ContractService;
import com.school_system.service.SecurityService;
import com.school_system.util.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class ContractServiceImpl implements ContractService {
    private final StudentRepository studentRepository;
    private final ContractRepository contractRepository;
    private final ContactRepository contactRepository;
    private HttpServletResponse httpServletResponse;
    private final StringRedisTemplate redisTemplate;
    private final SecurityService securityService;

    @Override
    @Transactional
    @CacheEvict(value = "contracts", key = "#result.data.id", condition = "false")
    public ResponseObject<ContractResponse> createContract(ContractRequest contractRequest) throws BadRequestException {
        Optional<Contract> contractOptional = contractRepository.findByContractNumber(contractRequest.getContractNumber());
        if (contractOptional.isPresent()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            return ResponseObject.<ContractResponse>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Vertragsnummer existiert bereits!")
                    .build();
        }
        Student student = studentRepository.findById(contractRequest.getStudent().id()).orElseThrow(
                () -> new EntityNotFoundException("Schüler wurde nicht gefunden!")
        );
        if (student.isMarkedForDeletion()){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return ResponseObject.<ContractResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Sie können einen Vertrag zu diesem Schüler anlegen, weil der Schüler zur Löschung markiert ist!")
                    .build();
        }
        // a student can have just one contract of type x
        Optional<Contract> contractOptional1 = contractRepository.findContractsByStudentIdAndOverlap(contractRequest.getStudent().id(),
                contractRequest.getContractType(), contractRequest.getStartAt(), contractRequest.getEndAt());
        if (contractOptional1.isPresent()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
            return ResponseObject.<ContractResponse>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Vertragtype für Schüler/in  existiert bereits!")
                    .build();
        }

        if(!checkIfModulsValid(contractRequest.getModuls())){
            throw new BadRequestException("Mindestens eine Unterrichtsoption muss korrekt sein.");
        }
        Contract newContract = new Contract();
        BeanUtils.copyProperties(contractRequest, newContract,"student");
        newContract.getModuls().forEach(m -> m.setContract(newContract));
        newContract.setStudent(student);


        return ResponseObject.<ContractResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Vertrag wurde erfolgreich erstellt!")
                .data(Mapper.toContractResponse(contractRepository.save(newContract)))
                .build();
    }

    private boolean checkIfModulsValid(List<Modul> moduls) {
        return moduls.parallelStream()
                .allMatch(m -> {
                    // Ensure at least one lesson type is allowed
                    boolean atLeastOneLessonAllowed = m.isGroupLessonAllowed() || m.isSingleLessonAllowed();
                    // Check if group lessons are allowed and ensure the cost is greater than zero
                    boolean isGroupLessonValid = !m.isGroupLessonAllowed() || m.getGroupLessonCost().compareTo(BigDecimal.ZERO) > 0;
                    // Check if single lessons are allowed and ensure the cost is greater than zero
                    boolean isSingleLessonValid = !m.isSingleLessonAllowed() || m.getSingleLessonCost().compareTo(BigDecimal.ZERO) > 0;
                    // Ensure at least one type of lesson is allowed and that the conditions are met
                    return atLeastOneLessonAllowed && isGroupLessonValid && isSingleLessonValid;

                });
    }

    @Override
    @Transactional
    @Cacheable(value = "contracts", key = "#id", condition = "false")
    public ResponseObject<ContractResponse> getContract(Long id) {
        Optional<Contract> contract = contractRepository.findById(id);
        if (contract.isEmpty()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<ContractResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Vertrag wurde nicht gefunden!")
                    .build();
        }

        return ResponseObject.<ContractResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toContractResponse(contract.get()))
                .build();
    }

    @Override
    public ResponseObject<Page<ContractResponse>> getContracts(Pageable pageable) {

        Page<Contract> contract = contractRepository.findAll(pageable);
        if (contract.isEmpty()) {
            return ResponseObject.<Page<ContractResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Keine Verträge gefunden!")
                    .build();
        }
        return ResponseObject.<Page<ContractResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(contract.map(Mapper::toContractResponse))
                .build();
    }

    @Override
    public ResponseObject<Page<ContractResponse>> getContractsWithQuery(String query, ContractType contractType, ContractStatus contractStatus, Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = securityService.isAdmin();
        boolean isStudent = securityService.isStudent();



        Page<Contract> contract = null;
        if (isAdmin) {
            contract = contractRepository.findAllWithQuery(query, contractType, contractStatus, pageable);
        } else if (isStudent) {
            contract = contractRepository.findAllWithQueryAndStudent(query, contractType, contractStatus, UserUtils.getUserId(authentication), pageable);
        }

        if (contract == null || contract.isEmpty()) {
            return ResponseObject.<Page<ContractResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Keine Verträge gefunden!")
                    .build();
        }
        return ResponseObject.<Page<ContractResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(contract.map(Mapper::toContractResponse))
                .build();
    }

    @Override
    @Cacheable(value = "contracts", key = "'lists.' + #studentId", condition = "false")
    public ResponseObject<List<ContractResponse>> getContractByStudentId(Long studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isEmpty()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<List<ContractResponse>>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Schüler/in nicht gefunden!")
                    .build();
        }
        List<Contract> contract = contractRepository.findByStudentId(student.get().getId());
        if (contract.isEmpty()) {
            return ResponseObject.<List<ContractResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Keine Verträge für den Schüler gefunden!")
                    .build();
        }

        return ResponseObject.<List<ContractResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(contract.stream().map(Mapper::toContractResponse).toList())
                .build();
    }

    // TODO delete the id parameter if possible and use the id from ContractRequest
    @Override
    @Transactional
    @CachePut(value = "contracts", key = "#id", condition = "false")
    public ResponseObject<ContractResponse> updateContract(Long id, ContractRequest contractRequest) throws BadRequestException {

        Contract contract = contractRepository.findById(contractRequest.getId()).orElseThrow(
                () -> new EntityNotFoundException("Vertrag wurde nicht gefunden!")
        );

        if (!contract.getContractType().equals(contractRequest.getContractType())) {
            Student student = studentRepository.findById(contract.getStudent().getId()).orElseThrow(
                    () -> new EntityNotFoundException("Student wurde nicht gefunden!")
            );
            if (student.isMarkedForDeletion()){
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return ResponseObject.<ContractResponse>builder()
                        .status(ResponseObject.ResponseStatus.FAILED)
                        .message("Sie können der Vertrag zu diesem Schüler nicht aktualisieren, weil der Schüler zur Löschung markiert ist!")
                        .build();
            }
            boolean hasContractType = student.getContracts().stream().anyMatch(c -> c.getContractType().equals(contractRequest.getContractType()));
            if (hasContractType) {
                Optional<Contract> contractOptional1 = contractRepository.findContractsByStudentIdAndOverlap(contractRequest.getStudent().id(),
                        contractRequest.getContractType(), contractRequest.getStartAt(), contractRequest.getEndAt());
                if (contractOptional1.isPresent()) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_CONFLICT);
                    return ResponseObject.<ContractResponse>builder()
                            .status(ResponseObject.ResponseStatus.FAILED)
                            .message("Vertragstype für Schüler/in  existiert bereits!")
                            .build();
                }
            }

        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = UserUtils.getUsername(authentication);
        LocalDateTime dateNow = LocalDateTime.now();
        BeanUtils.copyProperties(contractRequest, contract, "moduls", "contact");
        // TODO manage Module Add, Update and Remove in seperate function will made the code simpler and easier to read
        // TODO And there will be no need to delete  all old moduls and add new ones
        contract.getModuls().clear();

        if ((contract.getContact() == null && contractRequest.getContact() != null) ||
              (contract.getContact() != null
                      && contractRequest.getContact() != null &&
                      !contract.getContact().equals(contractRequest.getContact()))) {
            Contact contact = new Contact();
            // update or create the id will provide this info
            BeanUtils.copyProperties(contractRequest.getContact(), contact);
            contact.setUpdatedBy(user);
            contact.setUpdatedAt(dateNow);
            // TODO Maybe remove this and change the Realtioship between entities
            contactRepository.save(contact);
            contract.setContact(contact);
        }
        if(!checkIfModulsValid(contractRequest.getModuls())){
            throw new BadRequestException("Mindestens eine Unterrichtsoption muss korrekt sein.");
        }
        for (Modul modul : contractRequest.getModuls()) {
            modul.setContract(contract);
            contract.getModuls().add(modul);
        }

        contract.setUpdatedBy(user);
        contract.setUpdatedAt(dateNow);
        return ResponseObject.<ContractResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Vertrag wurde erfolgreich aktualisiert.")
                .data(Mapper.toContractResponse(contractRepository.save(contract)))
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<String> deleteContract(Long id) {
        boolean condition = false;
        Contract contract = contractRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Vertrag mit der ID = %d wurde nicht gefunden!", id)));
        if (condition) {
            Set<String> keys = new HashSet<>();
            keys.add(id.toString());
            keys.add("lists." + contract.getStudent().getId().toString());
            redisTemplate.delete(keys);
        }
        contractRepository.delete(contract);
        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Vertrag erfolgreich gelöscht!")
                .data(contract.getStudent().getId().toString())
                .build();
    }


}
