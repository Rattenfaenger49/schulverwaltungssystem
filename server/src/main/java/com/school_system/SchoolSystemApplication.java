package com.school_system;



import com.school_system.config.MyAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableJpaAuditing
@EnableConfigurationProperties(MyAppProperties.class)
public class SchoolSystemApplication {


	public static void main(String[] args) {
		SpringApplication.run(SchoolSystemApplication.class, args);

	}


}

