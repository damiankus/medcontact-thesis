package com.medcontact.data.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.BasicUser;

@NoRepositoryBean
public interface AbstractUserRepository<T extends BasicUser, E extends Serializable> extends
        PagingAndSortingRepository<T, E> {

}