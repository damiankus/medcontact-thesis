package com.medcontact.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Specialty;

public interface SpecialtyRepository extends PagingAndSortingRepository<Specialty, Long> {

}
