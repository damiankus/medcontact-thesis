package com.medcontact.data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Patient;
import com.medcontact.data.model.domain.Reservation;

@Configuration
@EnableJpaRepositories(basePackages={ "com.medcontact.data.repository" })
@EntityScan(basePackages={ "com.medcontact.data.model" })
public class RepositoryConfiguration extends RepositoryRestMvcConfiguration {
	
	@Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.useHalAsDefaultJsonMediaType();
        config.exposeIdsFor(Patient.class, Doctor.class, Reservation.class);
    }
	
	@Bean 
	@Primary
	public ObjectMapper getMapper() {
		return super.halObjectMapper();
	}
}
