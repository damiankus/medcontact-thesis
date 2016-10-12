package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.medcontact.data.model.BasicUser;

public interface BasicUserRepository extends PagingAndSortingRepository<BasicUser, Long> {
	public Optional<BasicUser> findByEmail(String email);
	
	@Query("SELECT COUNT(user) FROM BasicUser user WHERE user.email = :email")
	public boolean isEmailAvailable(@Param("email") String email);
}