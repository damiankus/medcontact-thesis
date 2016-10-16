package com.medcontact.data.repository;

import com.medcontact.data.model.ScheduleTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<ScheduleTimeSlot, Long> {
	
	public List<ScheduleTimeSlot> findByDoctorId(long doctorId);
}
