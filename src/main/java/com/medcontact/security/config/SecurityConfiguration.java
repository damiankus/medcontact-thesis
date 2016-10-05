package com.medcontact.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.anyRequest().authenticated()
				.antMatchers("/patient/**").hasRole("PATIENT")
				.antMatchers("/doctor/**").hasRole("DOCTOR")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.and()
			.formLogin()
				.and()
			.httpBasic();
	}
	
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(getApplicationUserDetailsService());
	}
	
	@Bean
	public ApplicationUserDetailsService getApplicationUserDetailsService() {
		return new ApplicationUserDetailsService();
	}
}
