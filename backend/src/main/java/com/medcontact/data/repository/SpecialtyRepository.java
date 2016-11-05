package com.medcontact.data.repository;

import com.medcontact.data.model.domain.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

}
