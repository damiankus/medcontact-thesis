package com.medcontact.security.config;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.medcontact.data.model.ApplicationUser;

import lombok.Data;

@Entity
@Table(name="doctors")
@Data
public class Doctor extends ApplicationUser {
	private static final long serialVersionUID = -5663126536666561117L;

	private String title;
	private List<String> specialties;
	private String university;
	private String biography;
	private double rating;
	
	
	public Doctor(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	
}
