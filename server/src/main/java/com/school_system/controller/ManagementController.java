package com.school_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school_system.common.ResponseObject;
import com.school_system.dto.response.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ManagementController {



    @GetMapping("/dates/feiertage")
    public ResponseObject<FeiertageResponse> getFeiertage(@RequestParam String years,
                                          @RequestParam String state,
                                          @RequestParam Boolean all_states){

        HttpClient httpClient = HttpClient.newHttpClient();

        String apiUrl = String.format("https://get.api-feiertage.de/?years=%s&states=%s&katholisch=%s",
                years, state, all_states);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();
            return ResponseObject.<FeiertageResponse>builder()
                            .status(ResponseObject.ResponseStatus.SUCCESSFUL)
                            .data(objectMapper.readValue(response.body(), FeiertageResponse.class))
                            .build();
        } catch (Exception e) {
            // TODO throw custom exception
            throw new RuntimeException("Es ist ein Fehler beim Abrufen der Feiertage aufgetreten.");
        }
    }



}
