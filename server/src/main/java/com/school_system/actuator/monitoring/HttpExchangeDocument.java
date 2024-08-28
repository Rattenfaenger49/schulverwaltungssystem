package com.school_system.actuator.monitoring;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "httpExchanges")
public class HttpExchangeDocument {

    @Id
    private String id;
    private Instant timestamp;
    private HttpExchangeRequest request;
    private HttpExchangeResponse response;
    private HttpExchangePrincipal principal;
    private HttpExchangeSession session;
    private  Duration timeTaken;
    private  String username;
    private  String body;
}
