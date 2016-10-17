package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	public Optional<Doctor> findByUsername(String username);
}
