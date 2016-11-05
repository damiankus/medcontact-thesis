package com.medcontact.data.repository;

import com.medcontact.data.model.domain.Opinion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpinionRepository extends JpaRepository<Opinion, Long> {

}
