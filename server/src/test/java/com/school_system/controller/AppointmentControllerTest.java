package com.school_system.controller;

import com.school_system.config.Databases.TenantContext;
import com.school_system.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    AppointmentControllerTest(){
        TenantContext.setCurrentTenant("test-001");
    }

    @Test
    void getAppointments() {
    }

    @Test
    void getAppointment() {
    }

    @Test
    void getAttendees() {
    }

    @Test
    void createAppointments() {
    }

    @Test
    void addAttendees() {
    }

    @Test
    void removeAttendees() {
    }

    @Test
    void updateAppointment() {
    }

    @Test
    void deleteAppointment() {
    }
}