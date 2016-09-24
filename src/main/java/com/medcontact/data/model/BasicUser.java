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
	
	@Column(nullable=false, updatable=false, unique=true)
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
	
	/* Default constructor. We're setting default values for 
	 * the class members. */
	
	public BasicUser() {

		this.username = "default";
		this.password = "default";
		this.enabled = true;
		this.accountNonExpired = true;
		this.credentialsNonExpired = true;
		this.accountNonLocked = true;
		this.authorities = Arrays.asList(
				new SimpleGrantedAuthority(ApplicationRole.PATIENT.toString()));
		
		this.firstName = "default";
		this.lastName = "default";
		this.sex = Sex.OTHER.toString();
		this.email = "default@default.com";
	}
	
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
	
	public static BasicUser createDefaultUser() {
		return new BasicUser("", "", ApplicationRole.PATIENT, "", "", Sex.OTHER);
	}
	
	public <T extends BasicUser> void copyBasicData(T other) {
		this.username = other.getUsername();
		this.password = other.getPassword();
		this.enabled = other.isEnabled();
		this.accountNonExpired = other.isAccountNonExpired();
		this.credentialsNonExpired = other.isCredentialsNonExpired();
		this.accountNonLocked = other.isAccountNonLocked();
		this.authorities = other.getAuthorities();
		
		this.firstName = other.getFirstName();
		this.lastName = other.getLastName();
		this.sex = other.getSex();
		this.email = other.getEmail();
	}
	
	public static BasicUserBuilder getBuilder() {
		return new BasicUserBuilder();
	}
	
	/* 
	 * For the sake of convenience we provide a fluent API
	 * for building BasicUser objects.
	 *  */
	
	public abstract static class AbstractUserBuilder<T extends BasicUser> {
		protected T user;
				
		public AbstractUserBuilder<T> setUsername(String username) {
			user.setUsername(username);
			return this;
		}
		
		public AbstractUserBuilder<T> setPassword(String password) {
			user.setPassword(password);
			return this;
		}
		
		public AbstractUserBuilder<T> setFirstName(String firstName) {
			user.setFirstName(firstName);
			return this;
		}
		
		public AbstractUserBuilder<T> setLastName(String lastName) {
			user.setLastName(lastName);
			return this;
		}
		
		public AbstractUserBuilder<T> setEmail(String email) {
			user.setPassword(email);
			return this;
		}
		
		public AbstractUserBuilder<T> setSex(Sex sex) {
			user.setSex(sex.toString());
			return this;
		}
		
		public T build() {
			return this.user;
		}
	}
	
	public static class BasicUserBuilder<T extends BasicUser> extends AbstractUserBuilder<BasicUser> {
		public BasicUserBuilder() {
			this.user = new BasicUser("", "", ApplicationRole.PATIENT, "", "", Sex.OTHER);
		}
	}
}
