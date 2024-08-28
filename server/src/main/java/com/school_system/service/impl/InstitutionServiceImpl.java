package com.school_system.service.impl;

import com.school_system.common.ResponseObject;
import com.school_system.dto.request.InstitutionRequest;
import com.school_system.dto.response.InstitutionResponse;
import com.school_system.entity.school.Contact;
import com.school_system.entity.school.Institution;
import com.school_system.entity.school.Address;
import com.school_system.exception.ExistByNameException;
import com.school_system.mapper.Mapper;
import com.school_system.repository.ContactRepository;
import com.school_system.repository.InstitutionRepository;
import com.school_system.service.AddressService;
import com.school_system.service.ContactService;
import com.school_system.service.InstitutionService;


import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final AddressService addressService;
    private final ContactService contactService;
    private ContactRepository contactRepository;
    private HttpServletResponse httpServletResponse;

    @Transactional
    @Override
    public ResponseObject<InstitutionResponse> createInstitution(InstitutionRequest institutionRequest) {
        // TODO check if Institution has to be checked by email, Name, both or somthing else!
        Optional<Institution> optionalInstitution =  institutionRepository.findByName(institutionRequest.getName());
        if(optionalInstitution.isPresent()){
                throw new ExistByNameException(String.format("Die Geschäftsstelle mit dem Namen %s existiert bereits.", institutionRequest.getName()));
        }
        Address address = addressService.getExistingOrSaveNewAddress(institutionRequest.getAddress());
        List<Contact> contactList = institutionRequest.getContacts().stream().map(
                contactService::getExistingOrSaveNewContract
        ).toList();
        Institution institution = Institution
                .builder()
                .name(institutionRequest.getName())
                .address(address)
                .email(institutionRequest.getEmail())
                .phoneNumber(institutionRequest.getPhoneNumber())
                .build();
        contactList.forEach(contact -> contact.setInstitution(institution));
        institution.setContacts(contactList);
        return ResponseObject.<InstitutionResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toInstituionResponse(institutionRepository.save(institution)))
                .message("Geschäftsstelle erstellt!")
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<InstitutionResponse> getInstitution(Long id) {
        Optional<Institution> institution = institutionRepository.findById(id);
        if(institution.isPresent()) {
            return ResponseObject.<InstitutionResponse>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .data(Mapper.toInstituionResponse(institution.get()))
                    .build();
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        return ResponseObject.<InstitutionResponse>builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Geschäftsstelle nicht gefunden!")
                .build();
    }

    @Override
    public ResponseObject<Page<InstitutionResponse>> getInstitutions(Pageable pageable) {
        Page<Institution> institution = institutionRepository.findAll(pageable);
        if(institution.isEmpty()) {
            return ResponseObject.<Page<InstitutionResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Geschäftsstelle nicht gefunden!")
                    .build();
        }
        return ResponseObject.<Page<InstitutionResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(institution.map(Mapper::toInstituionResponse))
                .build();
    }

    @Override
    @Transactional
    public ResponseObject<InstitutionResponse> updateInstitution(InstitutionRequest institutionRequest) {
        Optional<Institution> optionalInstitution = institutionRepository.findById(institutionRequest.getId());
        if(optionalInstitution.isEmpty()) {

            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<InstitutionResponse>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Geschäftsstelle nicht gefunden!")
                    .build();
        }
        Institution institution = optionalInstitution.get();
        institution.setName(institutionRequest.getName());
        institution.setEmail(institutionRequest.getEmail());
        institution.setPhoneNumber(institutionRequest.getPhoneNumber());
        if (!institution.getAddress().equals(institutionRequest.getAddress())) {
            institution.setAddress(addressService.getExistingOrSaveNewAddress(institutionRequest.getAddress()));
        }

        if (!institutionRequest.getContacts().isEmpty()) {
            institutionRequest.getContacts().forEach(c -> {
                if(c.getInstitution() != null)
                    c.getInstitution().setId(institution.getId());
                else
                    c.setInstitution(institution);
            });
            contactRepository.saveAll(institutionRequest.getContacts());

        }

        return ResponseObject.<InstitutionResponse>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(Mapper.toInstituionResponse(institutionRepository.save(institution)))
                .message("Geschäftsstelle wurde erstellt!")
                .build();
    }

    @Override
    public ResponseObject<Page<InstitutionResponse>> getInstitutionsWithQuery(String query, Pageable pageable) {

        Page<Institution> institution = institutionRepository.searchInstitutions(query,pageable);
        if(institution.isEmpty()) {
            return ResponseObject.<Page<InstitutionResponse>>builder()
                    .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                    .message("Keine Geschäftsstellen gefunden!")
                    .build();

        }
        return ResponseObject.<Page<InstitutionResponse>>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .data(institution.map(Mapper::toInstituionResponse))
                .build();
    }

    @Override
    public ResponseObject<String> deleteInstitution(Long id) {
        Optional<Institution> institution = institutionRepository.findById(id);
        if(institution.isEmpty()) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ResponseObject.<String>builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Geschäftsstelle wurde nicht gefunden!")
                    .build();
        }
        institutionRepository.delete(institution.get());
        return ResponseObject.<String>builder()
                .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                .message("Geschäftsstelle wurde gelöscht!")
                .build();
    }
}
