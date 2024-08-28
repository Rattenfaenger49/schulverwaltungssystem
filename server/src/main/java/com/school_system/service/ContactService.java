package com.school_system.service;

import com.school_system.entity.school.Contact;

public interface ContactService {

    public Contact getExistingOrSaveNewContract(Contact contact);
}
