package com.school_system.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.school_system.Utils;
import com.school_system.common.ResponseObject;
import com.school_system.conf.BaseControllerTest;
import com.school_system.dto.response.TeacherResponse;

import com.school_system.dto.response.UserTable;
import com.school_system.entity.security.Teacher;
import com.school_system.entity.security.User;
import com.school_system.repository.TeacherRepository;
import com.school_system.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.ocsp.Req;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TeacherControllerTest extends BaseControllerTest {


    static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
    @Autowired
    private JwtService jwtService;
    @MockBean
    private TeacherRepository teacherRepository;
    @Test
    void getTeachersWithQuery() throws Exception {

        // given
        List<Teacher> teachers = Utils.createTeacherList();
        List<Long> ids = teachers.parallelStream().map(User::getId).toList();
        log.info("ids: {}", ids);
        // Erstellen einer Pageable-Instanz für die findAll-Methode
        Pageable pageable = Pageable.ofSize(teachers.size());

        // Erstellen einer PageImpl mit der Liste der Lehrer und der Pageable-Instanz
        Page<Teacher> page = new PageImpl<>(teachers, pageable, teachers.size());
        when(teacherRepository.findAll(any(Pageable.class))).thenReturn(page);

        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("http://localhost:8080/api/v1/teachers")
                .header("X-TenantID", tenantId)
                .header("Authorization", "Bearer "+ jwtService.generateAccessToken(Utils.getCustomUserDetails()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String body = result.getResponse().getContentAsString();

        ResponseObject<?> responseObject
                = objectMapper.readValue(body, ResponseObject.class);
        // then

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.SUCCESSFUL);

        // Get the data from the response
        Object responseData = responseObject.getData();

        // Convert responseData to a JSON string
        String responseDataJson = objectMapper.writeValueAsString(responseData);

        // Parse the JSON string to a JSON node
        JsonNode jsonNode = objectMapper.readTree(responseDataJson);
        assertThat(jsonNode.get("totalPages").asInt()).isEqualTo(1);
        assertThat(jsonNode.get("totalElements").asInt()).isEqualTo(20);
        assertThat(jsonNode.get("size").asInt()).isEqualTo(20);
        assertThat(jsonNode.get("empty").asBoolean()).isEqualTo(false);
        /*JsonNode contentNode = jsonNode.get("content");
        // check only five entities
        for (int i = 0; i < 5; i++) {
            int  randoumNum =  (int) (Math.random() * 20) + 1;
            JsonNode randomElement = contentNode.get(randoumNum);
            UserTable userTable = objectMapper.readValue(randomElement.toString(), UserTable.class);

            Teacher teacher = teachers.get(randoumNum);

            assertThat(userTable.getFirstName()).isEqualTo(teacher.getFirstName());
            assertThat(userTable.getLastName()).isEqualTo(teacher.getLastName());
            assertThat(userTable.getUsername()).isEqualTo(teacher.getUsername());
            assertThat(userTable.getPhoneNumber()).isEqualTo(teacher.getPhoneNumber());
            assertThat(userTable.getGender()).isEqualTo(teacher.getGender());


        }*/


    }




    @Test
    void test_get_teacher_by_id_should_success() throws Exception {
        // given
        Teacher teacher = Utils.createTeacher();
        // when
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(
                        "http://localhost:8080/api/v1/teachers/" + teacher.getId())
                        .contentType("application/json")
                        .header("X-TenantID", tenantId)
                        .header("Authorization", "Bearer "+ jwtService.generateAccessToken(Utils.getCustomUserDetails()));
        when(teacherRepository.findById(any())).thenReturn(Optional.of(teacher));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);

        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.SUCCESSFUL);
        TeacherResponse teacherResponse = Utils.toteacherResponse(objectMapper.writeValueAsString(responseObject.getData()));

        // then
        assertThat(teacherResponse.getId()).isEqualTo(teacher.getId());
        assertThat(teacherResponse.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(teacherResponse.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(teacherResponse.getUsername()).isEqualTo(teacher.getUsername());
        assertThat(teacherResponse.getPhoneNumber()).isEqualTo(teacher.getPhoneNumber());
        assertThat(teacherResponse.getGender()).isEqualTo(teacher.getGender());
        assertThat(teacherResponse.getEducation()).isEqualTo(teacher.getEducation());
        assertThat(teacherResponse.getQualifications()).isEqualTo(teacher.getQualifications());
    }
    @Test
    void test_get_teacher_by_id_with_id_as_not_a_number() throws Exception {
        // given
        Teacher teacher = Utils.createTeacher();
        // when
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(
                                "http://localhost:8080/api/v1/teachers/null" )
                        .contentType("application/json")
                        .header("X-TenantID", tenantId)
                        .header("Authorization", "Bearer "+ jwtService.generateAccessToken(Utils.getCustomUserDetails()));
        when(teacherRepository.findById(any())).thenReturn(Optional.empty());
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);
        assertThat(responseObject.getMessage()).isEqualTo("Endpunkt nicht gefunden oder fehlerhafte Params .");

    }
    @Test
    void test_get_teacher_by_id_with_invalid_id() throws Exception {
        // given
        Teacher teacher = Utils.createTeacher();
        // when
        RequestBuilder requestBuilder =
                MockMvcRequestBuilders.get(
                                "http://localhost:8080/api/v1/teachers/1200" )
                        .contentType("application/json")
                        .header("X-TenantID", tenantId)
                        .header("Authorization", "Bearer "+ jwtService.generateAccessToken(Utils.getCustomUserDetails()));
        when(teacherRepository.findById(any())).thenThrow(new EntityNotFoundException(String.format("Teacher with id = %d not found!", 1200)));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        ResponseObject<?> responseObject = Utils.getResponseObject(response);
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(responseObject.getStatus()).isEqualTo(ResponseObject.ResponseStatus.FAILED);


    }
    @Test
    void updateTeacher() {
    }

    @Test
    void undoDeleteTeacher() {
    }

    @Test
    void getTeachersFullname() {
    }

    @Test
    void generateDocumentation() {
    }

    @Test
    void assignStudentToTeacher() {
    }

    @Test
    void deleteTeacher() {
    }

    @Test
    void removeStudentToTeacherAssignment() {
    }
}