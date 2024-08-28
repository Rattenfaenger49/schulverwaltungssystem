package com.school_system.repository;

import com.school_system.entity.school.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{
    @Query("SELECT a FROM Address a " +
            "WHERE a.street = :#{#address.street} " +
            "AND a.streetNumber = :#{#address.streetNumber} " +
            "AND a.city = :#{#address.city} " +
            "AND a.state = :#{#address.state} " +
            "AND a.country = :#{#address.country} " +
            "AND a.postal = :#{#address.postal}")
    Optional<Address> findBySpecifications(@Param("address") Address address);



}
