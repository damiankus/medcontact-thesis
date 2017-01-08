package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.domain.BasicUser;

public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {
	public Optional<BasicUser> findByEmail(String email);
	public Optional<BasicUser> findByUsername(String username);
}