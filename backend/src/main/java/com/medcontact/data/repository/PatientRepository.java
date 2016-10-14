package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	public Optional<Patient> findByUsername(String username);
}
