package com.medcontact.data.model.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.medcontact.data.model.enums.ApplicationRole;
import org.springframework.security.core.GrantedAuthority;

import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name="authorities")
@Data
public class ApplicationAuthority implements GrantedAuthority {
	private static final long serialVersionUID = 4460930272284533497L;

	@Id
	private long id;
	
	@Column(nullable=false, unique=true)
	@Enumerated(EnumType.STRING)
	@NonNull
	private ApplicationRole role;
	
	@Override
	public String getAuthority() {
		return this.role.toString();
	}
	
	public long getId() {
		return role.getValue();
	}
	
	/* This method is defined only for the purpose
	 * of satisfying Hibernate's requirements for
	 * persisted objects' fields. */
	
	public void setId(long id) {
		
	}
}
