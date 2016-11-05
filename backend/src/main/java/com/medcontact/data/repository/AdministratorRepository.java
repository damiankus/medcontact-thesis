package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import com.medcontact.data.model.domain.Admin;

public interface AdministratorRepository extends JpaRepository<Admin, Long> {
	
	@PreAuthorize("#id == authentication.principal.id")
	public Admin findOne(Long id);
	
	public Optional<Admin> findByUsername(String username);
}
