package com.medcontact.data.model;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.NonNull;


@Entity
@Table(name="users")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="role")
@Data
public class BasicUser implements UserDetails {
	private static final long serialVersionUID = 2679949213300447361L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected long id;
	
	/* Authentication details required by the UserDetails interface. */
	
	@Column(nullable=false, updatable=false)
	@NonNull
	protected String username;

	@Column(nullable=false)
	@NonNull
	protected String password;
	
	@Column(nullable=false)
	protected boolean enabled;
	
	@Column(nullable=false)
	protected boolean accountNonExpired;
	
	@Column(nullable=false)
	protected boolean credentialsNonExpired;
	
	@Column(nullable=false)
	protected boolean accountNonLocked;
	
	@Transient
	@NonNull
	protected Collection<? extends GrantedAuthority> authorities;
	
	/* Custom user data */
	
	@Column(nullable=false)
	@NonNull
	protected String firstName;
	
	@Column(nullable=false)
	@NonNull
	protected String lastName;
	
	@Column(nullable=false)
	@NonNull
	protected String sex;
	
	@Column(nullable=false)
	@NonNull
	protected String email;
	
	
	public BasicUser(String email, String password, ApplicationRole role, 
			 String firstName, String lastName, Sex sex) {

		this.username = email;
		this.password = password;
		this.enabled = true;
		this.accountNonExpired = true;
		this.credentialsNonExpired = true;
		this.accountNonLocked = true;
		this.authorities = Arrays.asList(
				new SimpleGrantedAuthority(role.toString()));
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.sex = sex.toString();
		this.email = email;
	}
}
