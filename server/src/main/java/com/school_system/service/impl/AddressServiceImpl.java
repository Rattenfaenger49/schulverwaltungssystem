package com.school_system.service.impl;

import com.school_system.entity.school.Address;
import com.school_system.repository.AddressRepository;
import com.school_system.service.AddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AddressServiceImpl  implements AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    private Environment environment;
    @Override
    @Cacheable(value = "addresses", key = "#address.id",  condition = "false")
    public Address getExistingOrSaveNewAddress(Address address) {

        Optional<Address> existingAddress = addressRepository.findBySpecifications(address);
        if (existingAddress.isPresent()) {
            return existingAddress.get();
        }
        address.setId(null);
        return addressRepository.save(address);
    }
}
