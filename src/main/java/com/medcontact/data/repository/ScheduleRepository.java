package com.medcontact.data.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.ScheduleTimeSlot;

public interface ScheduleRepository extends PagingAndSortingRepository<ScheduleTimeSlot, Long> {
	
	public List<ScheduleTimeSlot> findByDoctorId(long doctorId);
}
