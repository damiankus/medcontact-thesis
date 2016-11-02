package com.medcontact.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medcontact.data.model.FileEntry;

public interface FileRepository extends JpaRepository<FileEntry, Long> {
	
	public List<FileEntry> findByFileOwnerUsername(String username);
	
	@Query("SELECT f FROM FileEntry f WHERE f.fileOwner.id = :ownerId AND f.name = :filename")
	public FileEntry findByFilenameAndOwnerId(@Param("filename") String filename, @Param("ownerId") Long ownerId);
}
