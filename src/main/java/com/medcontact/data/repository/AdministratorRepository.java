package com.medcontact.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Admin;

public interface AdministratorRepository extends PagingAndSortingRepository<Admin, Long> {

}
