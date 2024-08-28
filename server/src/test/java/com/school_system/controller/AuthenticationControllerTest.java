package com.school_system.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.school_system.Utils;
import com.school_system.common.ResponseObject;
import com.school_system.conf.BaseControllerTest;
import com.school_system.dto.request.AuthenticationRequest;

import com.school_system.dto.request.RefreshTokenRequest;
import com.school_system.dto.request.UpdatePasswordRequest;
import com.school_system.dto.response.AuthenticationResponse;
import com.school_system.entity.security.ConfirmationToken;
import com.school_system.entity.security.User;
import com.school_system.repository.ConfirmationTokenRepository;
import com.school_system.service.JwtService;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerTest extends BaseControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtService jwtService;
    @MockBean
    private ConfirmationTokenRepository confirmationTokenRepository;

    public AuthenticationControllerTest() {
        super(); // Call superclass constructor

    }



    @Test
    @Order(1)
    void test_login_as_admin_with_currect_credentials() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin@example.de");
        request.setPassword("123456789");
        String requestBody = objectMapper.writeValueAsString(request);

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/login")
                .header("X-TenantID",tenantId)
                .accept(MediaType.APPLICATION_JSON).content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);


        JSONObject responseObject = new JSONObject(jsonResponse);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        String at = responseObject.getString("accessToken");
        String rt = responseObject.getString("refreshToken");

        assertThat(at).isNotBlank();
        assertThat(rt).isNotBlank();



    }
    @Test
    @Order(2)
    void test_login_as_admin_with_invalid_password() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("admin@example.de");
        request.setPassword("WRONG_PASSWORD");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/login")
                .header("X-TenantID", tenantId)
                .accept(MediaType.APPLICATION_JSON).content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        ResponseObject<?> responseObject = Utils.getResponseObject(response);


        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        // Deserialize JSON string to ResponseObject
        assertThat(responseObject).isNotNull();


        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();

    }



    @Test
    @Order(2)
    void test_login_as_admin_with_invalid_username() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("invalidexample.de");
        request.setPassword("123456789");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/login")
                .header("X-TenantID", tenantId)
                .accept(MediaType.APPLICATION_JSON).content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        // Deserialize JSON string to ResponseObject
        assertThat(responseObject).isNotNull();

        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
    }
    @Test
    @Order(3)
    void test_login_as_admin_with_not_exist_username() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername("invalid@example.de");
        request.setPassword("123456789");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/login")
                .header("X-TenantID", tenantId)
                .accept(MediaType.APPLICATION_JSON).content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        // Deserialize JSON string to ResponseObject
        assertThat(responseObject).isNotNull();

        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
    }

    @Test
    void test_logout_with_invalid_token() throws Exception {
        // Given

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/logout")
                .header("X-TenantID", tenantId)
                .header("Authorization", "Bearer ftghgiughregkl3tlkgmnlksdngkldnhöldsmfaslöfgdfhmdflögsfg" );
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
    }
    @Test
    void test_logout_without_token() throws Exception {
        // Given

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/logout")
                .header("X-TenantID", tenantId)
                .header("Authorization", "Bearer ");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
    }
    @Test
    void test_logout_without_auth_header() throws Exception {
        // Given

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/logout")
                .header("X-TenantID", tenantId);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

    }
    @Test
    void test_logout_with_correct_credentials() throws Exception {
        // Given

        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/logout")
                .header("X-TenantID", tenantId)
                .header("Authorization", "Bearer "+ jwtService.generateAccessToken(Utils.getCustomUserDetails()));

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    }
    @Test
    void test_refresh_token_with_valid_token() throws Exception {
        // Given

        String refreshToken = jwtService.generateRefreshToken(Utils.getCustomUserDetails());
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        log.info("refresh token: " + refreshToken);
        String requestBody = objectMapper.writeValueAsString(request);
        log.info("requestBody: {}", requestBody);
        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/refresh")
                .accept(MediaType.APPLICATION_JSON).content(requestBody)
                .header("X-TenantID", tenantId)
                .contentType(MediaType.APPLICATION_JSON);


        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);

        AuthenticationResponse responseObject = objectMapper.readValue(jsonResponse, AuthenticationResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseObject.getAccessToken()).isNotNull();
        assertThat(responseObject.getRefreshToken()).isNotNull();

    }
    @Test
    void test_refresh_token_with_invalid_signature() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("eyJhbGciOiJIUzUxMiJ9.eyJ0ZW5hbnRJZCI6InRlc3QtMDAxIiwiaWQiOjEsInVzZXJuYW1lIjoiYWRtaW5AeWFzYS1pdC5kZSIsImlhdCI6MTcxNTM3MzY1NSwiaXNzIjoic2VsZiIsInN1YiI6ImFkbWluQHlhc2EtaXQuZGUiLCJleHAiOjE3MTU5Nzg0NTV9.nW5-FDqoeQhk4Q8X2gPBzwQ7BCpy9BdysVXj2W04D1WXwUwO3tuDuOv3pNBCHWRUsda6kmQIPwMTlN1Gzumw");
        String requestBody = objectMapper.writeValueAsString(request);
        // When and Then
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/v1/auth/refresh")
                .accept(MediaType.APPLICATION_JSON).content(requestBody)
                .header("X-TenantID", tenantId)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();

        ResponseObject<?> responseObject = Utils.getResponseObject(response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
        assertThat(responseObject.getMessage()).isEqualTo("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");


    }
    @Test
    void confirm() {
        // requestHeaders["Authorization"] = `Bearer ${token}`;
    }

    @Test
    void set_password_with_invalid_token() throws Exception {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                "",
                "123456789",
                "123456789",
                "confirmation-token"
        );

        when(confirmationTokenRepository.findByToken(any())).thenReturn(Optional.empty());
        String requestBody = objectMapper.writeValueAsString(updatePasswordRequest);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("http://localhost:8080/api/v1/auth/set-password")
                        .header("X-TenantID", tenantId)
                        .contentType("application/json")
                        .content(requestBody);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
        assertThat(responseObject.getMessage()).isEqualTo("Token nicht gefunden. Bitte fordern Sie einen neuen Token an.");
    }
    @Test
    void set_password_with_valid_token() throws Exception {
        String confToken ="2b8843de-5d63-4841-bef1-74ccaddf5bb5";

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                "",
                "123456789",
                "123456789",
                confToken
        );
        ConfirmationToken confirmationToken =
                new ConfirmationToken(
                        null,
                        confToken,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(1),
                        null, User.builder().id(1L).build());
        when(confirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));
        String requestBody = objectMapper.writeValueAsString(updatePasswordRequest);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("http://localhost:8080/api/v1/auth/set-password")
                        .header("X-TenantID", tenantId)
                        .contentType("application/json")
                        .content(requestBody);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.SUCCESSFUL);
        assertThat(responseObject.getData()).isNull();
        assertThat(responseObject.getMessage()).isEqualTo("Passwort erfolgreich aktualisiert!");
    }
    @Test
    void set_password_with_expired_token() throws Exception {
        String confToken ="2b8843de-5d63-4841-bef1-74ccaddf5bb5";

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                "",
                "123456789",
                "123456789",
                confToken
        );
        ConfirmationToken confirmationToken =
                new ConfirmationToken(
                        null,
                        confToken,
                        LocalDateTime.now(),
                        LocalDateTime.now().minusHours(1),
                        null, User.builder().id(1L).build());
        when(confirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));
        String requestBody = objectMapper.writeValueAsString(updatePasswordRequest);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("http://localhost:8080/api/v1/auth/set-password")
                        .header("X-TenantID", tenantId)
                        .contentType("application/json")
                        .content(requestBody);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
        assertThat(responseObject.getMessage()).isEqualTo("Token ist abgelaufen!");
    }
    @Test
    void set_password_with_invalid_password() throws Exception {
        String confToken ="2b8843de-5d63-4841-bef1-74ccaddf5bb5";

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                "",
                "123456789s",
                "123456789d",
                confToken
        );
        ConfirmationToken confirmationToken =
                new ConfirmationToken(
                        null,
                        confToken,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(1),
                        null, User.builder().id(1L).build());
        when(confirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));
        String requestBody = objectMapper.writeValueAsString(updatePasswordRequest);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("http://localhost:8080/api/v1/auth/set-password")
                        .header("X-TenantID", tenantId)
                        .contentType("application/json")
                        .content(requestBody);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();
        assertThat(responseObject.getMessage()).isEqualTo("Passwörter stimmen nicht überein!");
    }
    @Test
    void set_password_without_confirmation_token() throws Exception {

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                "",
                "123456789",
                "123456789",
                null
        );
        ConfirmationToken confirmationToken =
                new ConfirmationToken(
                        null,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(1),
                        null, User.builder().id(1L).build());
        when(confirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));
        String requestBody = objectMapper.writeValueAsString(updatePasswordRequest);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("http://localhost:8080/api/v1/auth/set-password")
                        .header("X-TenantID", tenantId)
                        .contentType("application/json")
                        .content(requestBody);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();

    }
    @Test
    void set_password_wit_confirmation_token_empty() throws Exception {
        // The same will happen for Passwords
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest(
                "",
                "123456789",
                "123456789",
                ""
        );
        ConfirmationToken confirmationToken =
                new ConfirmationToken(
                        null,
                        "",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(1),
                        null, User.builder().id(1L).build());
        when(confirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));
        String requestBody = objectMapper.writeValueAsString(updatePasswordRequest);
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("http://localhost:8080/api/v1/auth/set-password")
                        .header("X-TenantID", tenantId)
                        .contentType("application/json")
                        .content(requestBody);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseObject).isNotNull();
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getData()).isNull();

    }
}