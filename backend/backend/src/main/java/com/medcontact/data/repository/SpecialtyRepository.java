package com.medcontact.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.domain.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
	public Optional<Specialty> findByName(String specialtyName);
	public List<Specialty> findByNameStartingWith(String specialtyNameFragment);
}
