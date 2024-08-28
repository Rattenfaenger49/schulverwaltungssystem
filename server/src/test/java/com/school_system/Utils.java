package com.school_system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.school_system.common.ResponseObject;
import com.school_system.dto.CustomUserDetails;
import com.school_system.dto.response.TeacherResponse;
import com.school_system.entity.school.Address;
import com.school_system.entity.security.Role;
import com.school_system.entity.security.Teacher;
import com.school_system.entity.security.User;
import com.school_system.enums.authentication.RoleName;
import com.school_system.enums.school.Gender;

import com.school_system.mapper.Mapper;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class Utils {
    static ObjectMapper objectMapper = new ObjectMapper();

    private static final Faker faker = new Faker();

    public static CustomUserDetails getCustomUserDetails() {
        Set<Role> role = new HashSet<>();
        Role role1 = new Role();
        role1.setName(RoleName.ADMIN);
        role1.setDescription("admin");
        role.add(role1);


        User user = User.builder()
                .gender(Gender.MALE)
                .id(1L)
                .firstName("Muster")
                .username("admin@example.de")
                .lastName("Muster")
                .roles(role)
                .phoneNumber("+49000000000")
                .build();
        return Mapper.toCustomUserDetails(user);

    }

    public static List<Teacher> createTeacherList() {

        List<Teacher> teacherList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {

            teacherList.add(createTeacher());
        }
        return teacherList;
    }

    public static Teacher createTeacher() {

        return  Teacher.builder()
                .id(faker.number().randomNumber())
                .gender(Gender.values()[faker.random().nextInt(Gender.values().length)])
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .phoneNumber(faker.phoneNumber().cellPhone())
                .comment(faker.lorem().sentence())
                .password(faker.internet().password())
                .username(faker.internet().emailAddress())
                .accountNonExpired(faker.random().nextBoolean())
                .accountNonLocked(faker.random().nextBoolean())
                .credentialsNonExpired(faker.random().nextBoolean())
                .enabled(faker.random().nextBoolean())
                .verified(faker.random().nextBoolean())
                .hasProvidedAllInfo(faker.random().nextBoolean())
                .markedForDeletion(faker.random().nextBoolean())
                .updatedAt(LocalDateTime.now())
                .updatedBy(faker.name().fullName())
                .qualifications(faker.educator().course())
                .education(faker.educator().university())
                .roles(generateRoles())
                .address(generateAddress())
                .build();
    }

    private static Set<Role> generateRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder()
                .name(RoleName.values()[Utils.faker.random().nextInt(RoleName.values().length)])
                .description(Utils.faker.lorem().sentence())
                .build());
        // Hier können weitere Rollen hinzugefügt werden, wenn gewünscht
        return roles;
    }

    private static Address generateAddress() {
        return Address.builder()
                .street(Utils.faker.address().streetAddress())
                .streetNumber(Utils.faker.address().buildingNumber())
                .city(Utils.faker.address().city())
                .state(Utils.faker.address().state())
                .country(Utils.faker.address().country())
                .postal(Integer.parseInt(Utils.faker.number().digits(5)))
                .updatedAt(LocalDateTime.now())
                .updatedBy(Utils.faker.name().fullName())
                .build();
    }
    public static ResponseObject<?> getResponseObject(MockHttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        if(response == null) return null;
        String jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(jsonResponse, ResponseObject.class);
    }
    public static TeacherResponse toteacherResponse(String response) throws JsonProcessingException, UnsupportedEncodingException {
        if(response == null) return null;
        return objectMapper.readValue(response, TeacherResponse.class);
    }
}

