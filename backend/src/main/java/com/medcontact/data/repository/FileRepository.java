package com.medcontact.data.repository;

import java.util.List;


import com.medcontact.data.model.domain.FileEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntry, Long> {
	
	public List<FileEntry> findByFileOwnerUsername(String username);
}
