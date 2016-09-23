package com.medcontact.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Opinion;

public interface OpinionRepository extends PagingAndSortingRepository<Opinion, Long> {

}
