package com.medcontact.data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages={ "com.medcontact.data.repository" })
@EntityScan(basePackages={ "com.medcontact.data.model" })
public class RepositoryConfiguration {

}
