package com.school_system.repository;

import com.school_system.entity.school.Contact;
import com.school_system.entity.school.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c WHERE c.email = :#{#cotract.email} AND" +
            " c.phoneNumber = :#{#cotract.phoneNumber} AND " +
            "c.firstName = :#{#cotract.firstName} AND " +
            "c.lastName = :#{#cotract.lastName} AND " +
            "c.gender = :#{#cotract.gender}")
    Optional<Contact> findBySpecifications(Contact cotract);
}
