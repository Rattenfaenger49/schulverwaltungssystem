package com.school_system.service.impl;

import com.school_system.entity.school.Contact;
import com.school_system.entity.school.Contract;
import com.school_system.repository.ContactRepository;
import com.school_system.service.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;
@AllArgsConstructor
@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    private final Environment environment;

    @Override
    @Cacheable(value = "contacts", key ="#contact.id",  condition = "false")
    public Contact getExistingOrSaveNewContract(Contact contact) {
        Optional<Contact> contractOptional= contactRepository.findBySpecifications(contact);
        if(contractOptional.isPresent()){
            return contractOptional.get();
        }
        // id will be created on the DB level
        contact.setId(null);
        return  contactRepository.save(contact);
    }
}
