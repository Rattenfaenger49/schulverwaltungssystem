package com.school_system.service;

import com.school_system.entity.school.Address;

public interface AddressService {
    Address getExistingOrSaveNewAddress(Address address);
}
