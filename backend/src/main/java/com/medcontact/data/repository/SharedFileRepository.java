package com.medcontact.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.domain.SharedFile;


public interface SharedFileRepository extends JpaRepository<SharedFile, Long> {
	public List<SharedFile> findByReservationId(Long id);
}