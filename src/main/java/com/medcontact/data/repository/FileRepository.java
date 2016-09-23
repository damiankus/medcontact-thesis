package com.medcontact.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.FileEntry;

public interface FileRepository extends PagingAndSortingRepository<FileEntry, Long> {

}
