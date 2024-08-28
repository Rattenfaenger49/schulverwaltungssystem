package com.school_system.conf;

import com.school_system.config.MyAppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest
@AutoConfigureMockMvc
@EnableTransactionManagement
@EnableScheduling
@EnableConfigurationProperties(MyAppProperties.class)
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    protected String tenantId = "test-001";

    protected BaseControllerTest(){

    }

}