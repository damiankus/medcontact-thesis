package com.medcontact.data.model.domain;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.medcontact.data.model.enums.ApplicationRole;
import com.medcontact.data.model.enums.Sex;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;
import lombok.NonNull;


@Entity
@Table(name="users")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="user_type")
@Data
public class BasicUser implements UserDetails {
	private static final long serialVersionUID = 2679949213300447361L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	/* Authentication details required by the UserDetails interface. */

	@Column(nullable=false)
	@NonNull
	protected String username;	
	
	@Column(nullable=false)
	@NonNull
	
	/* Using the JsonProperty annotation allows us
	 * to not include the password in the serialized 
	 * JSON object returned to the client. */
	
	@JsonProperty(access=Access.WRITE_ONLY)
	protected String password;
	
	@Column(nullable=false)
	@JsonIgnore
	protected boolean enabled;
	
	@Column(nullable=false)
	@JsonIgnore
	protected boolean accountNonExpired;
	
	@Column(nullable=false)
	@JsonIgnore
	protected boolean credentialsNonExpired;
	
	@Column(nullable=false)
	@JsonIgnore
	protected boolean accountNonLocked;
	
	@Column
	@Enumerated(EnumType.STRING)
	protected ApplicationRole role;
	
	/* Custom user data */
	
	@Column(nullable=false, unique=true)
	@NonNull
	@JsonProperty(access=Access.WRITE_ONLY)
	protected String email;
	
	@Column(nullable=false)
	@NonNull
	protected String firstName;
	
	@Column(nullable=false)
	@NonNull
	protected String lastName;
	
	@Transient
	@JsonIgnore
	protected Sex sex;
	
	/* Default constructor. We're setting default values for 
	 * the class members. */
	
	public BasicUser() {
		this.username = "";
		this.password = "";
		this.enabled = true;
		this.accountNonExpired = true;
		this.credentialsNonExpired = true;
		this.accountNonLocked = true;
		this.role = ApplicationRole.PATIENT;
		this.firstName = "";
		this.lastName = "";
		this.sex = Sex.OTHER;
		this.email = "";
	}
	
	public static BasicUser createDefaultUser() {
		return new BasicUser();
	}
	
	public <T extends BasicUser> void copyBasicData(T other) {
		this.password = other.getPassword();
		this.enabled = other.isEnabled();
		this.accountNonExpired = other.isAccountNonExpired();
		this.credentialsNonExpired = other.isCredentialsNonExpired();
		this.accountNonLocked = other.isAccountNonLocked();
		this.role = other.getRole();
		
		this.firstName = other.getFirstName();
		this.lastName = other.getLastName();
		this.sex = other.getSex();
		this.email = other.getEmail();
	}
	
	/* We use the email as the user name and thus we must 
	 * after changing the email address we need to modify the user name too.
	 * Note that we modify the email address so that it doesn't contain
	 * upper case letters. It should make email comparison easier (we use 
	 * emails as user names). */
	
	public void setEmail(String email) {
		this.email = email.toLowerCase();
		this.username = email.toLowerCase();
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
			user.setEmail(email);
			return this;
		}
		
		public AbstractUserBuilder<T> setSex(Sex sex) {
			user.setSex(sex);
			return this;
		}
		
		public AbstractUserBuilder<T> valueOf(BasicUser otherUser) {
			user.setPassword(otherUser.getPassword());
			user.setEmail(otherUser.getEmail());
			user.setFirstName(otherUser.getFirstName());
			user.setLastName(otherUser.getLastName());
			user.setSex(otherUser.getSex());
			
			return this;
		}
		
		public T build() {
			return this.user;
		}
	}
	
	public static class BasicUserBuilder extends AbstractUserBuilder<BasicUser> {
		public BasicUserBuilder() {
			this.user = new BasicUser();
		}
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(
				new SimpleGrantedAuthority(
						this.role.toString()));
	}
}
