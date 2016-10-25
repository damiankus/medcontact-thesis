package com.medcontact.security.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true, proxyTargetClass=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Autowired
	AuthenticationFailureHandler authenticationFailureHandler;
	
	@Autowired
	LogoutHandler logoutHandler;
	
	@Autowired
	LogoutSuccessHandler logoutSuccessHandler;
	
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/patients/**").hasRole("PATIENT")
				.antMatchers("/doctors/**").hasRole("DOCTOR")
				.antMatchers("/admins/**").hasRole("ADMIN")
				.antMatchers("/signup/**").permitAll()
				.antMatchers("/home/**").permitAll()
				.anyRequest().permitAll()
				.and()
			.formLogin()
				.loginProcessingUrl("/login")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(authenticationFailureHandler)
				.and()
			.logout()
				.logoutUrl("/logout")
				.addLogoutHandler(logoutHandler)
				.logoutSuccessHandler(logoutSuccessHandler)
				.and()
			.csrf().disable()
			.httpBasic();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(getApplicationUserDetailsService())
			.passwordEncoder(getPasswordEncoder());
	}
	
    @Bean
    public AuthenticationSuccessHandler authSuccessHandler() {
    	return new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				response.setStatus(HttpStatus.OK.value());
			}
		};
    }
    
    @Bean
    public AuthenticationFailureHandler authFailureHandler() {
    	return new AuthenticationFailureHandler() {
			
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}
		};
	}
    
    @Bean
    public LogoutHandler customLogoutHandler() {
    	return new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				try {
					SecurityContextHolder.clearContext();
					HttpSession session = request.getSession();
					session.invalidate();
					request.logout();
					
				} catch (ServletException e) {
					logger.warn(e.getMessage());
				}
				
				logger.info("Logged out");
			}
		};
    }
    
    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
    	return new LogoutSuccessHandler() {
			
			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
					throws IOException, ServletException {
				
				String message = "Logged out";
				response.setStatus(HttpStatus.OK.value());
				
				try (PrintWriter writer = response.getWriter()) {
					writer.write(message);
				}
				
			}
		};
    }
	
	@Bean
	public ApplicationUserDetailsService getApplicationUserDetailsService() {
		return new ApplicationUserDetailsService();
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
