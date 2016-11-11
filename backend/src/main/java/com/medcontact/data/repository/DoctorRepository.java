package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import com.medcontact.data.model.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	public Optional<Doctor> findByUsername(String username);
	
	@PreAuthorize("#id == authentication.principal.id")
	public Doctor findOne(Long id);
	
}
