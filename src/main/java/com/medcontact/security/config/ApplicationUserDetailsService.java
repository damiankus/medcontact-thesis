package com.medcontact.security.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.medcontact.data.model.BasicUser;
import com.medcontact.data.repository.BasicUserRepository;

public class ApplicationUserDetailsService implements UserDetailsService {
	
	@Autowired
	private BasicUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<BasicUser> user = userRepository.findByUsername(username);
		
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("Authentication failed");
		}
		
		System.out.println("User with role: " + user.get().getClass().getName() + " has logged in.");
		System.out.println("Authorities: ");
		user.get().getAuthorities().forEach(a -> System.out.println(a.getAuthority()));
		return user.get();
	}

}
