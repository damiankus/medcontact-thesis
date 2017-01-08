package com.medcontact.security.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.medcontact.data.model.domain.BasicUser;
import com.medcontact.data.repository.BasicUserRepository;

public class ApplicationUserDetailsService implements UserDetailsService {
	
	@Autowired
	private BasicUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<BasicUser> user = userRepository.findByEmail(email);
		
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("Authentication failed");
		}
		
		return user.get();
	}
}
