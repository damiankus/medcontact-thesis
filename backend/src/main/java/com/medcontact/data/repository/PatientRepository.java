package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.method.P;

import com.medcontact.data.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	
//	@PreAuthorize("#id == authentication.principal.id")
	public Patient findOne(@P("id") Long id);
	
	public Optional<Patient> findByUsername(String username);
}
