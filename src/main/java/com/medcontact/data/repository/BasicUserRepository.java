package com.medcontact.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.BasicUser;

public interface BasicUserRepository extends PagingAndSortingRepository<BasicUser, Long> {

}