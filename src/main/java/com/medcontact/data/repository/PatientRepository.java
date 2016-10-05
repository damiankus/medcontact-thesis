package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Patient;

public interface PatientRepository extends PagingAndSortingRepository<Patient, Long> {
	public Optional<Patient> findByUsername(String username);
}
