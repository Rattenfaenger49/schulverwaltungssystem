package com.school_system.actuator.monitoring;

import com.school_system.config.MongoDBRestarter;
import com.school_system.repository.CustomHttpExchangeRepository;
import com.school_system.service.impl.EmailService;
import com.school_system.util.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class CustomTraceRepository implements HttpExchangeRepository {

    private final CustomHttpExchangeRepository customHttpExchangeRepository;
    private final EmailService emailService;
    private static boolean hasNotifiedAdmin = false;

    @Override
    public List<HttpExchange> findAll() {
        try {
            List<HttpExchangeDocument> httpExchangeDocuments = customHttpExchangeRepository.findAll();
            if (httpExchangeDocuments.isEmpty()) {
                return Collections.emptyList();
            }
            List<HttpExchange> httpExchanges = httpExchangeDocuments.parallelStream().map(this::mapDocumentToHttpExchange).toList();
            hasNotifiedAdmin = false;
            return httpExchanges;
        } catch (Exception e) {
            log.error(e.getMessage());
            if (!hasNotifiedAdmin) {
                emailService.sendErrorNotification(e.getMessage(), "Add HttpExchanges! Note: Code will try to restart the Mongo Automaticly if its work then there is nothing to worry about");
                hasNotifiedAdmin = true;
            }
            return Collections.emptyList();
        }
    }


    @Override
    public void add(HttpExchange exchange) {
        try {
            if (exchange.getRequest().getUri().getPath().contains("/actuator"))
                return;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = UserUtils.getUsername(authentication);
            // create the document map it to custom class
            HttpExchangeDocument httpExchangeDocument = new HttpExchangeDocument();
            HttpExchangeRequest request = new HttpExchangeRequest();
            BeanUtils.copyProperties(exchange.getRequest(), request);
            httpExchangeDocument.setRequest(request);

            HttpExchangeResponse response = new HttpExchangeResponse();
            BeanUtils.copyProperties(exchange.getResponse(), response);
            httpExchangeDocument.setResponse(response);

            HttpExchangePrincipal httpExchangePrincipal = new HttpExchangePrincipal();
            if (exchange.getPrincipal() != null) {
                httpExchangePrincipal.setName(exchange.getPrincipal().getName());
            } else {
                httpExchangePrincipal.setName(null); // Handle null case explicitly
            }
            httpExchangeDocument.setPrincipal(httpExchangePrincipal);

            HttpExchangeSession httpExchangeSession = new HttpExchangeSession();
            if (exchange.getSession() != null) {
                httpExchangeSession.setId(exchange.getSession().getId());
            } else {
                httpExchangeSession.setId(null);
            }

            httpExchangeDocument.setSession(httpExchangeSession);

            httpExchangeDocument.setTimestamp(exchange.getTimestamp());
            httpExchangeDocument.setTimeTaken(exchange.getTimeTaken());
            httpExchangeDocument.setUsername(username);
            customHttpExchangeRepository.save(httpExchangeDocument);
            // set has notifiedAdmin to false if it succeed to sae any records
            hasNotifiedAdmin = false;

        } catch (Exception e) {
            log.error(e.getMessage());
            if (!hasNotifiedAdmin) {
                emailService.sendErrorNotification(e.getMessage(), "Add HttpExchanges! Note: Code will try to restart the Mongo Automaticly if its work then there is nothing to worry about");
                hasNotifiedAdmin = true;
                MongoDBRestarter.restartMongoDB();
            }
        }
    }

    public HttpExchange mapDocumentToHttpExchange(HttpExchangeDocument document) {
        // Extract values and create HttpExchange object
        HttpExchange httpExchange = new HttpExchange(
                document.getTimestamp(),
                mapRequest(document.getRequest()),
                mapResponse(document.getResponse()),
                mapPrincipal(document.getPrincipal()),
                mapSession(document.getSession()),
                document.getTimeTaken()
        );


        return httpExchange;
    }


    private HttpExchange.Request mapRequest(HttpExchangeRequest requestDocumnet) {
        if (requestDocumnet == null) {
            return null;
        }
        HttpExchange.Request request = new HttpExchange.Request(
                requestDocumnet.getUri(),
                requestDocumnet.getRemoteAddress(),
                requestDocumnet.getMethod(),
                requestDocumnet.getHeaders()
        );
        return request;
    }

    private HttpExchange.Response mapResponse(HttpExchangeResponse responseDocumnet) {
        if (responseDocumnet == null) {
            return null;
        }
        HttpExchange.Response response = new HttpExchange.Response(
                responseDocumnet.getStatus(),
                responseDocumnet.getHeaders());

        return response;
    }

    private HttpExchange.Principal mapPrincipal(HttpExchangePrincipal principalDocumnet) {
        if (principalDocumnet == null) {
            return null;
        }
        return new HttpExchange.Principal(principalDocumnet.getName());
    }

    private HttpExchange.Session mapSession(HttpExchangeSession sessionDocumnet) {
        if (sessionDocumnet == null) {
            return null;
        }
        return new HttpExchange.Session(sessionDocumnet.getId());
    }

}
