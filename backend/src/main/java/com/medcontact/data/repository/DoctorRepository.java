package com.medcontact.data.repository;

import java.util.Optional;


import com.medcontact.data.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	public Optional<Doctor> findByUsername(String username);
}
