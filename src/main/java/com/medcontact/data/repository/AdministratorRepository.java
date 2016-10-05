package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Admin;

public interface AdministratorRepository extends PagingAndSortingRepository<Admin, Long> {
	public Optional<Admin> findByUsername(String username);
}
