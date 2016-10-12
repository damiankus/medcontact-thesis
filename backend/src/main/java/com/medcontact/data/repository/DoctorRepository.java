package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Doctor;

public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long> {
	public Optional<Doctor> findByUsername(String username);
}
