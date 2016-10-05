package com.medcontact.data.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.FileEntry;

public interface FileRepository extends PagingAndSortingRepository<FileEntry, Long> {
	
	public List<FileEntry> findByFileOwnerUsername(String username);
}
