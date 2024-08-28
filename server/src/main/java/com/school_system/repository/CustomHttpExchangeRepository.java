package com.school_system.repository;



import com.school_system.actuator.monitoring.HttpExchangeDocument;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CustomHttpExchangeRepository extends MongoRepository<HttpExchangeDocument, String> {

}
