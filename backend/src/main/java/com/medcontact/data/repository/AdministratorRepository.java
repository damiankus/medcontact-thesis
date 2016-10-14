package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.Admin;

public interface AdministratorRepository extends JpaRepository<Admin, Long> {
	public Optional<Admin> findByUsername(String username);
}
